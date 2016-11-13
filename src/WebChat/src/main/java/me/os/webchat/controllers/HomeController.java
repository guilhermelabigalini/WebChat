/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.controllers;

import java.util.List;
import me.os.webchat.rooms.IRoom;
import me.os.webchat.rooms.impl.memory.InMemoryRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author guilherme
 */
@Controller
public class HomeController {

    private final InMemoryRoomService roomService = new InMemoryRoomService();

    @RequestMapping("/")
    public ModelAndView index() {
        List<IRoom> rooms = roomService.getRooms(null);

        return new ModelAndView("index", "rooms", rooms);
    }

    @RequestMapping("/room")
    public ModelAndView greeting(
            @RequestParam(value = "roomId", required = true) Integer roomId) {

        IRoom room = roomService.getRoom(roomId);
        
        return new ModelAndView("room", "room", room);
    }
}
