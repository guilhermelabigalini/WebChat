package me.os.webchat.rooms.impl.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import me.os.webchat.rooms.BroadcastException;
import me.os.webchat.rooms.ChatMessage;
import me.os.webchat.rooms.ChatMessageBuilder;
import me.os.webchat.rooms.ChatUser;
import me.os.webchat.rooms.FullRoomException;
import me.os.webchat.rooms.IRoom;
import me.os.webchat.rooms.IUserCommuncationChannel;
import me.os.webchat.rooms.UserAlreadyLoggedException;

class InMemoryRoom implements IRoom {

    private final int MaxUsersPerRoom = 10;

    public InMemoryRoom(int id, String name) {
        super();
        this.id = id;
        this.name = name;
        this.loggedUsers = new ArrayList<>();
        this.userVsSession = new HashMap<>();
    }

    private int id;
    private String name;
    private final List<ChatUser> loggedUsers;
    private final HashMap<String, IUserCommuncationChannel> userVsSession;

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
        return Collections.unmodifiableList(loggedUsers);
    }

    @Override
    public void joinUser(ChatUser user, IUserCommuncationChannel channel) throws UserAlreadyLoggedException, FullRoomException, BroadcastException {

        if (loggedUsers.size() >= MaxUsersPerRoom) {
            throw new FullRoomException();
        }

        if (isUserPresent(user)) {
            throw new UserAlreadyLoggedException();
        }

        this.loggedUsers.add(user);

        storeUserChannel(user.getDisplayName(), channel);

        ChatMessage ulMessage = ChatMessageBuilder.buildUserListMessage(this, user);

        broadcastMessage(ulMessage);

        ChatMessage joinedMessage = ChatMessageBuilder.buildJoinedMessage(this, user);

        broadcastMessage(joinedMessage);
    }

    private boolean isUserPresent(ChatUser cu) {
        return this.loggedUsers.stream().anyMatch(u -> u.getDisplayName().equals(cu.getDisplayName()));
    }

    @Override
    public void removeUser(ChatUser user) throws BroadcastException {

        storeUserChannel(user.getDisplayName(), null);

        this.loggedUsers.removeIf(u -> u.getDisplayName().equals(user.getDisplayName()));

        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MESSAGETYPE_LEAVE);
        message.setFrom(user.getDisplayName());

        broadcastMessage(message);
    }

    @Override
    public void broadcastMessage(ChatMessage message) throws BroadcastException {

        List<ChatUser> users = getLoggedUsers();

        for (ChatUser cu : users) {
            if (cu.getDisplayName().equals(message.getFrom())
                    || (!message.isReserved() || cu.getDisplayName().equals(message.getTo()))) {

                IUserCommuncationChannel userSession = getUserChannel(cu.getDisplayName());

                if (userSession == null) {
                    continue;
                }

                if (!userSession.isActive()) {
                    storeUserChannel(cu.getDisplayName(), null);
                } else {
                    userSession.send(message);
                }
            }
        }
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
