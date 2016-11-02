/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.api;

import java.io.IOException;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import me.os.webchat.rooms.BroadcastException;
import me.os.webchat.rooms.IUserCommuncationChannel;

/**
 *
 * @author guilherme
 */
public class WebSocketUserChannel implements IUserCommuncationChannel {

    private final Session session;

    public WebSocketUserChannel(Session session) {
        this.session = session;
    }
    
    @Override
    public void send(Object message) throws BroadcastException {
        try {
            this.session.getBasicRemote().sendObject(message);
        } catch (EncodeException | IOException ex) {
            throw new BroadcastException(ex);
        } 
    }

    @Override
    public boolean isActive() {
        return this.session.isOpen();
    }    
}
