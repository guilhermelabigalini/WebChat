/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.websocket.DecodeException;
import javax.websocket.EncodeException;
import me.os.webchat.rooms.ChatMessage;

/**
 *
 * @author guilherme
 */
public class ChatMessageEncoderHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String encode(ChatMessage msg) throws EncodeException {
        try {
            return MAPPER.writeValueAsString(msg);
        } catch (JsonProcessingException ex) {
            throw new EncodeException(msg, ex.getMessage(), ex);
        }
    }

    public static ChatMessage decode(String s) throws DecodeException {
        try {
            return MAPPER.readValue(s, ChatMessage.class);
        } catch (IOException ex) {
            throw new DecodeException(s, ex.getMessage(), ex);
        }
    }
}
