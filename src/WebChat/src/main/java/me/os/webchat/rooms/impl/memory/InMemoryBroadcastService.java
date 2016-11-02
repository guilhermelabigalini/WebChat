/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.rooms.impl.memory;

import java.util.HashMap;
import java.util.List;
import me.os.webchat.rooms.BroadcastException;
import me.os.webchat.rooms.ChatMessage;
import me.os.webchat.rooms.ChatMessageBuilder;
import me.os.webchat.rooms.ChatUser;
import me.os.webchat.rooms.FullRoomException;
import me.os.webchat.rooms.IBroadcastService;
import me.os.webchat.rooms.IUserCommuncationChannel;
import me.os.webchat.rooms.InvalidRoomException;
import me.os.webchat.rooms.Room;
import me.os.webchat.rooms.UserAlreadyLoggedException;

/**
 *
 * @author guilherme
 */
public class InMemoryBroadcastService implements IBroadcastService {
    
    private final static HashMap<String, IUserCommuncationChannel> userVsSession = new HashMap<>();

    private static void storeUserChannel(int room, String login, IUserCommuncationChannel value) {
        String key = room + "." + login;
        userVsSession.put(key, value);
    }

    private static IUserCommuncationChannel getUserChannel(int room, String login) {
        String key = room + "." + login;
        return userVsSession.get(key);
    }
    
    @Override
    public void joinUser(Room targetRoom, ChatUser user, IUserCommuncationChannel channel) throws FullRoomException, UserAlreadyLoggedException, BroadcastException {
        
        targetRoom.joinUser(user);

        storeUserChannel(targetRoom.getId(), user.getDisplayName(), channel);

        ChatMessage ulMessage = ChatMessageBuilder.buildUserListMessage(targetRoom, user);
        
        broadcastMessage(targetRoom, ulMessage);

        ChatMessage joinedMessage = ChatMessageBuilder.buildJoinedMessage(targetRoom, user);

        broadcastMessage(targetRoom, joinedMessage);
    }

    @Override
    public void userExit(Room targetRoom, ChatUser user) throws InvalidRoomException, BroadcastException {

        if (targetRoom == null) {
            throw new InvalidRoomException();
        }

        storeUserChannel(targetRoom.getId(), user.getDisplayName(), null);

        targetRoom.removeUser(user.getDisplayName());

        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MESSAGETYPE_LEAVE);
        message.setFrom(user.getDisplayName());

        broadcastMessage(targetRoom, message);
    }

    @Override
    public void broadcastMessage(Room targetRoom, ChatMessage message) throws BroadcastException {
        
        List<ChatUser> users = targetRoom.getLoggedUsers();

        for (ChatUser cu : users) {
            if (cu.getDisplayName().equals(message.getFrom())
                    || (!message.isReserved() || cu.getDisplayName().equals(message.getTo()))) {
                
                IUserCommuncationChannel userSession = getUserChannel(targetRoom.getId(), cu.getDisplayName());
                
                if (userSession == null)
                    continue;
                
                if (!userSession.isActive()) {
                    storeUserChannel(targetRoom.getId(), cu.getDisplayName(), null);
                } else {
                    userSession.send(message);
                }
            }
        }
    }
}
