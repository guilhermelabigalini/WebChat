package me.os.webchat.rooms;

import java.util.List;

public interface IRoomService {

    List<IRoom> getRooms(String name);

    IRoom getRoom(int roomId);

}
