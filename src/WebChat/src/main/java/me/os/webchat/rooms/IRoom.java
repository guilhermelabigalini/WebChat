/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.os.webchat.rooms;

import java.util.List;

/**
 *
 * @author guilherme
 */
public interface IRoom {

    int getId();

    List<ChatUser> getLoggedUsers();

    String getName();

    void removeUser(ChatUser userId) throws BroadcastException;

    void joinUser(ChatUser user, IUserCommuncationChannel channel) throws FullRoomException, UserAlreadyLoggedException, BroadcastException;

    void broadcastMessage(ChatMessage message) throws BroadcastException;
}
