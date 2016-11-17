package me.os.webchat.api;


import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import me.os.webchat.rooms.BroadcastException;
import me.os.webchat.rooms.ChatMessage;
import me.os.webchat.rooms.ChatUser;
import me.os.webchat.rooms.FullRoomException;
import me.os.webchat.rooms.IRoom;
import me.os.webchat.rooms.IRoomService;
import me.os.webchat.rooms.InvalidRoomException;
import me.os.webchat.rooms.RoomServiceFactory;
import me.os.webchat.rooms.UserAlreadyLoggedException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SpringChatServer extends TextWebSocketHandler {

    private static final Logger Log = LoggerFactory.getLogger(SpringChatServer.class);
    
    private static class QueryParam {
        public int RoomId;
        public String displayName;

        public QueryParam(int RoomId, String displayName) {
            this.RoomId = RoomId;
            this.displayName = displayName;
        }
    }

    @Autowired
    private RoomServiceFactory roomServiceFactory;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Log.info("afterConnectionClosed");
        
        close(getParams(session));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        
        // http://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
        // UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().getFirst(key);
        
        Log.info("afterConnectionEstablished");
        
        join(getParams(session), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Log.info("handleTextMessage");
        message(getParams(session), ChatMessageEncoderHelper.decode(message.getPayload()), session);
    }
    
    private QueryParam getParams(WebSocketSession session) throws UnsupportedEncodingException {
        MultiValueMap<String, String> map = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams();
        int roomId = Integer.parseInt(map.getFirst("roomId"));
        String displayName = map.getFirst("displayName");
        displayName = URLDecoder.decode(displayName, "UTF-8");
        
        return new QueryParam(roomId, displayName);
    }
    
    private void join(
            QueryParam params,
            WebSocketSession session) throws InvalidRoomException, FullRoomException, UserAlreadyLoggedException, BroadcastException {

        Log.debug("session started");
        
        IRoomService roomService = roomServiceFactory.getRoomService();

        IRoom targetRoom = roomService.getRoom(params.RoomId);
        ChatUser user = new ChatUser(params.displayName);

        targetRoom.joinUser(user, new WebSocketSessionChannel(session));
    }

    private void close(QueryParam params) throws InvalidRoomException, BroadcastException {

        IRoomService roomService = roomServiceFactory.getRoomService();
        
        IRoom targetRoom = roomService.getRoom(params.RoomId);

        ChatUser user = new ChatUser(params.displayName);

        targetRoom.removeUser(user);
    }

    private void message(
            QueryParam params,
            ChatMessage message, WebSocketSession session) throws
            InvalidRoomException,
            BroadcastException {

        //Set<Session> activeSessions = session.getOpenSessions();
        //Log.debug("message: " + message + " send to total clients: " + activeSessions.size());

        IRoomService roomService = roomServiceFactory.getRoomService();
        
        message.setFrom(params.displayName);

        IRoom targetRoom = roomService.getRoom(params.RoomId);

        targetRoom.broadcastMessage(message);
    }
}
