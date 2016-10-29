package me.os.webchat.rooms;

import java.util.List;

public interface IRoomService {

    List<Room> getRooms(String name);

    Room getRoom(int roomId);

    void joinRoom(int roomId, ChatUser user) throws InvalidRoomException, FullRoomException, UserAlreadyLoggedException;

    void leaveRoom(int roomId, String userId) throws InvalidRoomException;

    void broadcastMessage(Integer room, ChatMessage message) throws InvalidRoomException, BroadcastException;
}
