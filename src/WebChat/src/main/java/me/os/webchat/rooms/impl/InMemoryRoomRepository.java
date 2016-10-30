package me.os.webchat.rooms.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import me.os.webchat.rooms.ChatMessage;

import org.springframework.stereotype.Service;

import me.os.webchat.rooms.ChatUser;
import me.os.webchat.rooms.FullRoomException;
import me.os.webchat.rooms.IRoomService;
import me.os.webchat.rooms.InvalidRoomException;
import me.os.webchat.rooms.Room;
import me.os.webchat.rooms.UserAlreadyLoggedException;
import me.os.webchat.rooms.BroadcastException;

@Service
public class InMemoryRoomRepository //implements IRoomService 
{

    private final int MaxUsersPerRoom = 10;

    private final static List<Room> rooms = new ArrayList<>();

    private final static HashMap<String, Session> userVsSession = new HashMap<>();

    public static void setUser(int room, String login, Session value) {
        String key = room + "." + login;
        userVsSession.put(key, value);
    }

    public static Session getUser(int room, String login) {
        String key = room + "." + login;
        return userVsSession.get(key);
    }

    static {
        rooms.add(new Room(1, "Cars"));
        rooms.add(new Room(2, "Music"));
        rooms.add(new Room(2, "Books"));
        rooms.add(new Room(2, "Movies"));
    }

    //@Override
    public List<Room> getRooms(String name) {

        Stream<Room> response = rooms.stream();

        if (name != null && name.length() > 0) {
            final String nameFilter = name.toLowerCase();
            response = response.filter(r -> r.getName().toLowerCase().indexOf(nameFilter) >= 0);
        }

        return response.collect(Collectors.toList());
    }

    //@Override
    public Room getRoom(int id) {
        return rooms.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }

    //@Override
    public void joinRoom(int roomId, ChatUser user, Session session) throws InvalidRoomException, FullRoomException, UserAlreadyLoggedException {
        Room targetRoom = this.getRoom(roomId);

        if (targetRoom == null) {
            throw new InvalidRoomException();
        }

        if (targetRoom.getRoomState().getLoggedUsers().size() >= MaxUsersPerRoom) {
            throw new FullRoomException();
        }

        if (targetRoom.getRoomState().isUserPresent(user)) {
            throw new UserAlreadyLoggedException();
        }

        targetRoom.getRoomState().getLoggedUsers().add(user);

        setUser(roomId, user.getDisplayName(), session);

        ChatMessage ulMessage = new ChatMessage();
        ulMessage.setType(ChatMessage.MESSAGETYPE_USERLIST);
        String userList = targetRoom.getRoomState().getLoggedUsers()
                .stream()
                .map(cu -> "\"" + cu.getDisplayName() + "\"")
                .reduce((result, element) -> result + "," + element).orElse(null);
        ulMessage.setTo(user.getDisplayName());
        ulMessage.setBody("[" + userList + "]");
        ulMessage.setReserved(true);
        internalBroadcastMessage(targetRoom, ulMessage);

        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MESSAGETYPE_JOINED);
        message.setFrom(user.getDisplayName());
        setUser(roomId, user.getDisplayName(), session);

        internalBroadcastMessage(targetRoom, message);
    }

    //@Override
    public void leaveRoom(int roomId, String userId) throws InvalidRoomException {
        Room targetRoom = this.getRoom(roomId);

        if (targetRoom == null) {
            throw new InvalidRoomException();
        }

        setUser(roomId, userId, null);

        targetRoom.getRoomState().removeUser(userId);

        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MESSAGETYPE_LEAVE);
        message.setFrom(userId);

        internalBroadcastMessage(targetRoom, message);
    }

//    public void handleMessage(Integer room, ChatMessage message, Session fromSession) throws InvalidRoomException, BroadcastException {
//
//        Room targetRoom = this.getRoom(room);
//
//        if (targetRoom == null) {
//            throw new InvalidRoomException();
//        }
//
//        if (message.getType().equals(ChatMessage.MESSAGETYPE_MESSAGE)){
//            message.setFrom(fromSession.getId());
//            internalBroadcastMessage(targetRoom, message);
//        }
//
//        if (message.getType().equals(ChatMessage.MESSAGETYPE_WANTTOJOIN)){
//            joinRoom(targetRoom, user);
//        }
//        
//        internalBroadcastMessage(targetRoom, message);
//    }
    //@Override
    public void broadcastMessage(Integer room, ChatMessage message, Session fromSession) throws InvalidRoomException, BroadcastException {

        Room targetRoom = this.getRoom(room);

        if (targetRoom == null) {
            throw new InvalidRoomException();
        }

        internalBroadcastMessage(targetRoom, message);
    }

    private void internalBroadcastMessage(Room targetRoom, ChatMessage message) {
        List<ChatUser> users = targetRoom.getRoomState().getLoggedUsers();

        for (ChatUser cu : users) {
            if (cu.getDisplayName().equals(message.getFrom())
                    || (!message.isReserved() || cu.getDisplayName().equals(message.getTo()))) {
                try {
                    Session userSession = getUser(targetRoom.getId(), cu.getDisplayName());

                    if (!userSession.isOpen()) {
                        setUser(targetRoom.getId(), cu.getDisplayName(), null);
                    } else {
                        userSession.getBasicRemote().sendObject(message);
                    }

                } catch (IOException | EncodeException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
