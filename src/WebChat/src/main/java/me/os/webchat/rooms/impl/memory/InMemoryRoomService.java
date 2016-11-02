package me.os.webchat.rooms.impl.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.os.webchat.rooms.IRoomService;

import org.springframework.stereotype.Service;

import me.os.webchat.rooms.Room;

@Service
public class InMemoryRoomService implements IRoomService 
{
    private final static List<Room> rooms = new ArrayList<>();

    static {
        rooms.add(new Room(1, "Cars"));
        rooms.add(new Room(2, "Music"));
        rooms.add(new Room(2, "Books"));
        rooms.add(new Room(2, "Movies"));
    }

    @Override
    public List<Room> getRooms(String name) {

        Stream<Room> response = rooms.stream();

        if (name != null && name.length() > 0) {
            final String nameFilter = name.toLowerCase();
            response = response.filter(r -> r.getName().toLowerCase().contains(nameFilter));
        }

        return response.collect(Collectors.toList());
    }

    @Override
    public Room getRoom(int id) {
        return rooms.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }
}
