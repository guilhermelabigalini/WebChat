package me.os.webchat.api.legacy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import javax.websocket.CloseReason;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import me.os.webchat.api.ChatMessageEncoderHelper;

import me.os.webchat.rooms.*;
import me.os.webchat.rooms.impl.memory.InMemoryRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(
        value = "/chat/{room}/{displayName}",
        encoders = {ChatServer.ChatMessageEncoder.class},
        decoders = {ChatServer.ChatMessageDecoder.class})
public class ChatServer {

    public static class ChatMessageEncoder implements Encoder.Text<ChatMessage> {

        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String encode(ChatMessage object) throws EncodeException {
            return ChatMessageEncoderHelper.encode(object);
        }

        @Override
        public void init(javax.websocket.EndpointConfig config) {
        }

        @Override
        public void destroy() {
        }
    }

    public static class ChatMessageDecoder implements Decoder.Text<ChatMessage> {

        @Override
        public ChatMessage decode(String s) throws DecodeException {
            return ChatMessageEncoderHelper.decode(s);
        }

        @Override
        public boolean willDecode(String s) {
            try {
                ChatMessageEncoderHelper.decode(s);
                return true;
            } catch (DecodeException ex) {
                return false;
            }
        }

        @Override
        public void init(javax.websocket.EndpointConfig config) {
        }

        @Override
        public void destroy() {
        }

    }
    
    private static final Logger Log = LoggerFactory.getLogger(ChatServer.class);

    private final IRoomService roomService = new InMemoryRoomService();

    @OnOpen
    public void open(
            @PathParam("room") Integer room,
            @PathParam("displayName") String displayName,
            Session session) throws InvalidRoomException, FullRoomException, UserAlreadyLoggedException, BroadcastException {

        Log.debug("session started");

        IRoom targetRoom = roomService.getRoom(room);
        ChatUser user = new ChatUser(displayName);

        targetRoom.joinUser(user, new WebSocketUserChannel(session));
    }

    @OnClose
    public void close(CloseReason c, Session client,
            @PathParam("displayName") String displayName,
            @PathParam("room") Integer room) throws InvalidRoomException, BroadcastException {

        IRoom targetRoom = roomService.getRoom(room);

        ChatUser user = new ChatUser(displayName);

        targetRoom.removeUser(user);
    }

    @OnMessage
    public void message(
            @PathParam("room") Integer room,
            @PathParam("displayName") String displayName,
            ChatMessage message, Session session) throws
            InvalidRoomException,
            BroadcastException {

        Set<Session> activeSessions = session.getOpenSessions();
        Log.debug("message: " + message + " send to total clients: " + activeSessions.size());

        message.setFrom(displayName);

        IRoom targetRoom = roomService.getRoom(room);

        targetRoom.broadcastMessage(message);
    }
}
