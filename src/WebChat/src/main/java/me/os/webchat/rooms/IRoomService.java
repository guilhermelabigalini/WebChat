package me.os.webchat.rooms;

import java.util.List;

public interface IRoomService {

    List<Room> getRooms(String name);

    Room getRoom(int roomId);

}
