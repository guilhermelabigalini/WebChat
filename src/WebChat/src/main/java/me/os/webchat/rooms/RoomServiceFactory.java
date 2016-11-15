/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.rooms;

import me.os.webchat.rooms.impl.memory.InMemoryRoomService;
import me.os.webchat.rooms.impl.redis.RedisRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author guilherme
 */
@Component 
public class RoomServiceFactory {

    private final IRoomService instance;
    
    @Autowired
    public RoomServiceFactory(@Value("${app.chat.channel}") String channel) {
        
        if (channel.equalsIgnoreCase("memory"))
            this.instance = new InMemoryRoomService();
        else
            this.instance = new RedisRoomService();
    }
    
    public IRoomService createRoomService() {
        return instance;
    }
    
}
