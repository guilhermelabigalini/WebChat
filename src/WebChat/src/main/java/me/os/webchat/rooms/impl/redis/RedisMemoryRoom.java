package me.os.webchat.rooms.impl.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.os.webchat.rooms.BroadcastException;
import me.os.webchat.rooms.ChatMessage;
import me.os.webchat.rooms.ChatMessageBuilder;
import me.os.webchat.rooms.ChatUser;
import me.os.webchat.rooms.FullRoomException;
import me.os.webchat.rooms.IRoom;
import me.os.webchat.rooms.IUserCommuncationChannel;
import me.os.webchat.rooms.UserAlreadyLoggedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

class RedisMemoryRoom implements IRoom {

    class RoomSub extends JedisPubSub {

        private final Consumer<ChatMessage> consumer;

        public RoomSub(Consumer<ChatMessage> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onMessage(String channel, String message) {

            try {
                ChatMessage result = mapper.readValue(message, ChatMessage.class);
                this.consumer.accept(result);

            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    private final int MaxUsersPerRoom = 10;

    private static Logger LOG = LoggerFactory.getLogger(RedisMemoryRoom.class);
    
    private final ObjectMapper mapper = new ObjectMapper();
    private int id;
    private String name;
    private final HashMap<String, IUserCommuncationChannel> userVsSession;
    private final String channelName;
    private final String listName;

    public RedisMemoryRoom(int id, String name) {

        super();
        this.id = id;
        this.name = name;
        this.userVsSession = new HashMap<>();

        this.channelName = "room" + id;
        this.listName = "users" + id;
        readMessages();
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<ChatUser> getLoggedUsers() {
        return this.getUsers().stream().map(s -> new ChatUser(s)).collect(Collectors.toList());
    }

    @Override
    public void joinUser(ChatUser user, IUserCommuncationChannel channel) throws UserAlreadyLoggedException, FullRoomException, BroadcastException {

        List<ChatUser> loggedUsers = getLoggedUsers();

        if (loggedUsers.size() >= MaxUsersPerRoom) {
            throw new FullRoomException();
        }

        if (loggedUsers.stream().anyMatch(u -> u.getDisplayName().equals(user.getDisplayName()))) {
            throw new UserAlreadyLoggedException();
        }

        addUser(user.getDisplayName());

        storeUserChannel(user.getDisplayName(), channel);

        ChatMessage ulMessage = ChatMessageBuilder.buildUserListMessage(this, user);

        broadcastMessage(ulMessage);

        ChatMessage joinedMessage = ChatMessageBuilder.buildJoinedMessage(this, user);

        broadcastMessage(joinedMessage);
    }

    @Override
    public void removeUser(ChatUser user) throws BroadcastException {

        LOG.info("removing user " + user.getDisplayName() + " from room " + this.getId());
        
        storeUserChannel(user.getDisplayName(), null);

        removeUserFromRedis(user.getDisplayName());
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MESSAGETYPE_LEAVE);
        message.setFrom(user.getDisplayName());

        broadcastMessage(message);
    }

    private List<String> getUsers() {
        return JedisFactory.getInstance().useResource(jedis -> {
            return jedis.lrange(listName, 0, 1000);
        });
    }

    private void addUser(String name) {
        JedisFactory.getInstance().useResource(jedis -> {
            jedis.lpush(listName, name);
        });
    }

    private void removeUserFromRedis(String name) {
        JedisFactory.getInstance().useResource(jedis -> {
            jedis.lrem(listName, 1, name);
        });
    }

    private void readMessages() {
        
        LOG.info("starting to listen " + channelName);
                
        Runnable run2 = () -> {

            JedisFactory.getInstance().useResource(jedis -> {

                jedis.subscribe(new RoomSub(message -> {

                    LOG.info("received message from " + channelName + ": " + message);
                    
                    for (String cu : this.getLocalUsers()) {
                        if (cu.equals(message.getFrom())
                                || (!message.isReserved() || cu.equals(message.getTo()))) {

                            IUserCommuncationChannel userSession = getUserChannel(cu);

                            if (userSession == null) {
                                continue;
                            }

                            try {
                                if (!userSession.isActive()) {
                                    removeUser(new ChatUser(cu));
                                } else {
                                    userSession.send(message);
                                }
                            } catch (BroadcastException ex) {
                                storeUserChannel(cu, null);
                            }
                        }
                    }
                }), channelName);
            });
        };

        Thread t1 = new Thread(run2);

        t1.start();
    }

    @Override
    public void broadcastMessage(ChatMessage message) throws BroadcastException {

        JedisFactory.getInstance().useResource(jedis -> {
            try {
                LOG.info("sending message to " + channelName + ": " + message);
                
                String jsonMsg = mapper.writeValueAsString(message);

                jedis.publish(channelName, jsonMsg);

            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });
    }

    protected Set<String> getLocalUsers() {
        return userVsSession.keySet();
    }

    protected void storeUserChannel(String login, IUserCommuncationChannel value) {
        String key = login;
        userVsSession.put(key, value);
    }

    protected IUserCommuncationChannel getUserChannel(String login) {
        String key = login;
        return userVsSession.get(key);
    }
}
