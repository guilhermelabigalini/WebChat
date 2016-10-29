package me.os.webchat.api;

import me.os.webchat.rooms.ChatMessage;
import java.io.IOException;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import me.os.webchat.rooms.BroadcastException;

import org.springframework.beans.factory.annotation.Autowired;

import me.os.webchat.rooms.ChatUser;
import me.os.webchat.rooms.FullRoomException;
import me.os.webchat.rooms.IRoomService;
import me.os.webchat.rooms.InvalidRoomException;
import me.os.webchat.rooms.Room;
import me.os.webchat.rooms.UserAlreadyLoggedException;
import me.os.webchat.rooms.impl.InMemoryRoomRepository;

// http://stackoverflow.com/questions/21559260/how-do-i-pass-a-parameter-to-the-onopen-method-with-jee7-websockets
@ServerEndpoint(
        value = "/chat/{room}/{displayName}",
        encoders = {ChatMessageEncoder.class},
        decoders = {ChatMessageDecoder.class})
public class ChatServer {

    //@Autowired
    private InMemoryRoomRepository roomService = new InMemoryRoomRepository();

    @OnOpen
    public void open(
            @PathParam("room") Integer room,
            @PathParam("displayName") String displayName,
            Session session) throws InvalidRoomException, FullRoomException, UserAlreadyLoggedException {

        System.out.println("session started");

        ChatUser user = new ChatUser(displayName);

        roomService.joinRoom(room, user, session);
    }

    @OnClose
    public void close(CloseReason c, Session client,
            @PathParam("displayName") String displayName,
            @PathParam("room") Integer room) throws InvalidRoomException {
        roomService.leaveRoom(room, displayName);
        System.out.println("session closed");
    }

    @OnMessage
    public void message(
            @PathParam("room") Integer room,
            @PathParam("displayName") String displayName,
            ChatMessage message, Session session) throws IOException,
            EncodeException,
            InvalidRoomException,
            BroadcastException {

        Set<Session> activeSessions = session.getOpenSessions();
        System.out.println("message: " + message + " send to total clients: " + activeSessions.size());

        message.setFrom(displayName);

        roomService.broadcastMessage(room, message, session);
    }
}
