package me.os.webchat.api;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import me.os.webchat.rooms.ChatUser;
import me.os.webchat.rooms.IRoom;
import me.os.webchat.rooms.IRoomService;
import me.os.webchat.rooms.RoomServiceFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class RoomController {

    @Autowired
    private RoomServiceFactory roomServiceFactory;

    @RequestMapping(path = "/api/rooms", method = GET)
    public List<IRoom> getRooms(@RequestParam(value = "query", required = false) String name) {

        IRoomService roomService = roomServiceFactory.getRoomService();

        return roomService.getRooms(name);
    }

    @RequestMapping(path = "/api/rooms/{id}/loggedusers", method = GET)
    public ResponseEntity<List<ChatUser>> getRoomUsers(@PathVariable("id") Integer id) {

        IRoomService roomService = roomServiceFactory.getRoomService();

        IRoom r = roomService.getRoom(id);
        if (r != null) {
            return ResponseEntity.ok(r.getLoggedUsers());
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
