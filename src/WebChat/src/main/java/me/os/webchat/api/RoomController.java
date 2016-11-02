package me.os.webchat.api;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import me.os.webchat.rooms.ChatUser;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.os.webchat.rooms.Room;
import me.os.webchat.rooms.impl.memory.InMemoryRoomService;

@RestController
public class RoomController {

    //@Autowired
    //private IRoomService roomService;
    private final InMemoryRoomService roomService = new InMemoryRoomService();


    @RequestMapping(path = "/api/rooms", method = GET)
    public List<Room> getRooms(@RequestParam(value = "query", required = false) String name) {
        return this.roomService.getRooms(name);
    }

    @RequestMapping(path = "/api/rooms/{id}/loggedusers", method = GET)
    public ResponseEntity<List<ChatUser>> getRoomUsers(@PathVariable("id") Integer id) {
        Room r = this.roomService.getRoom(id);
        if (r != null) {
            return ResponseEntity.ok(r.getLoggedUsers());
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
