package me.os.webchat.api;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.os.webchat.rooms.IRoomService;
import me.os.webchat.rooms.Room;
import me.os.webchat.rooms.RoomState;
import me.os.webchat.rooms.impl.InMemoryRoomRepository;

@RestController
public class RoomController {

    //@Autowired
    //private IRoomService roomService;
private InMemoryRoomRepository roomService = new InMemoryRoomRepository();


    @RequestMapping(path = "/api/rooms", method = GET)
    public List<Room> getRooms(@RequestParam(value = "query", required = false) String name) {
        return this.roomService.getRooms(name);
    }

    @RequestMapping(path = "/api/rooms/{id}/loggedusers", method = GET)
    public ResponseEntity<RoomState> getRoomUsers(@PathVariable("id") Integer id) {
        Room r = this.roomService.getRoom(id);
        if (r != null) {
            return ResponseEntity.ok(r.getRoomState());
        }

        return new ResponseEntity<RoomState>(HttpStatus.NOT_FOUND);
    }
}
