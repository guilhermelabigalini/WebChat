package me.os.webchat.api;

import java.util.Set;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import me.os.webchat.rooms.BroadcastException;
import me.os.webchat.rooms.ChatMessage;
import me.os.webchat.rooms.ChatUser;
import me.os.webchat.rooms.FullRoomException;
import me.os.webchat.rooms.IRoom;
import me.os.webchat.rooms.IRoomService;
import me.os.webchat.rooms.InvalidRoomException;
import me.os.webchat.rooms.UserAlreadyLoggedException;
import me.os.webchat.rooms.impl.memory.InMemoryRoomService;

// http://stackoverflow.com/questions/21559260/how-do-i-pass-a-parameter-to-the-onopen-method-with-jee7-websockets
@ServerEndpoint(
        value = "/chat/{room}/{displayName}",
        encoders = {ChatMessageEncoder.class},
        decoders = {ChatMessageDecoder.class})
public class ChatServer {

    //@Autowired
    private final IRoomService roomService = new InMemoryRoomService();

    @OnOpen
    public void open(
            @PathParam("room") Integer room,
            @PathParam("displayName") String displayName,
            Session session) throws InvalidRoomException, FullRoomException, UserAlreadyLoggedException, BroadcastException {

        System.out.println("session started");

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
        System.out.println("message: " + message + " send to total clients: " + activeSessions.size());

        message.setRoom(room);
        message.setFrom(displayName);

        IRoom targetRoom = roomService.getRoom(room);
        
        targetRoom.broadcastMessage(message);
    }
}
