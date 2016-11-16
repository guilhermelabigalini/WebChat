/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.api;

import java.io.IOException;
import javax.websocket.EncodeException;
import me.os.webchat.rooms.BroadcastException;
import me.os.webchat.rooms.ChatMessage;
import me.os.webchat.rooms.IUserCommuncationChannel;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author guilherme
 */
public class WebSocketSessionChannel implements IUserCommuncationChannel {

    private final WebSocketSession session;

    public WebSocketSessionChannel(WebSocketSession session) {
        this.session = session;
    }
    
    @Override
    public void send(ChatMessage message) throws BroadcastException {
        try {
            this.session.sendMessage(new TextMessage(ChatMessageEncoderHelper.encode(message)));
        } catch (EncodeException | IOException ex) {
            throw new BroadcastException(ex);
        } 
    }

    @Override
    public boolean isActive() {
        return this.session.isOpen();
    }    
}
