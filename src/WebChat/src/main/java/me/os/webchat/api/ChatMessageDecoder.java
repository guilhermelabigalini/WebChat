/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.api;

import me.os.webchat.rooms.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 *
 * @author guilherme
 */
public class ChatMessageDecoder implements Decoder.Text<ChatMessage> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public ChatMessage decode(String s) throws DecodeException {
        try {
            return mapper.readValue(s, ChatMessage.class);
        } catch (IOException ex) {
            throw new DecodeException(s, ex.getMessage(), ex);
        }
    }

    @Override
    public boolean willDecode(String s) {
        try {
            mapper.readValue(s, ChatMessage.class);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

}
