/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.rooms;

/**
 *
 * @author guilherme
 */
public class ChatMessageBuilder {
    public static ChatMessage buildJoinedMessage(Room targetRoom, ChatUser user) {
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MESSAGETYPE_JOINED);
        message.setFrom(user.getDisplayName());

        return message;
    }
    public static ChatMessage buildUserListMessage(Room targetRoom, ChatUser user) {
        ChatMessage ulMessage = new ChatMessage();
        ulMessage.setType(me.os.webchat.rooms.ChatMessage.MESSAGETYPE_USERLIST);
        String userList = targetRoom.getLoggedUsers()
                .stream()
                .map(cu -> "\"" + cu.getDisplayName() + "\"")
                .reduce((result, element) -> result + "," + element).orElse(null);
        ulMessage.setTo(user.getDisplayName());
        ulMessage.setBody("[" + userList + "]");
        ulMessage.setReserved(true);

        return ulMessage;
    }
}
