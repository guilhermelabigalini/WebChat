package me.os.webchat.rooms.impl.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.os.webchat.rooms.IRoom;
import me.os.webchat.rooms.IRoomService;



public class RedisRoomService implements IRoomService 
{
    private final static List<RedisMemoryRoom> rooms = new ArrayList<>();

    static {
        rooms.add(new RedisMemoryRoom(1, "Cars"));
        rooms.add(new RedisMemoryRoom(2, "Music"));
        rooms.add(new RedisMemoryRoom(3, "Books"));
        rooms.add(new RedisMemoryRoom(4, "Movies"));
    }

    @Override
    public List<IRoom> getRooms(String name) {

        Stream<RedisMemoryRoom> response = rooms.stream();

        if (name != null && name.length() > 0) {
            final String nameFilter = name.toLowerCase();
            response = response.filter(r -> r.getName().toLowerCase().contains(nameFilter));
        }

        return response.collect(Collectors.toList());
    }

    @Override
    public IRoom getRoom(int id) {
        return rooms.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }
}
