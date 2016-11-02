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
public interface IBroadcastService {

    void joinUser(Room targetRoom, ChatUser user, IUserCommuncationChannel channel) throws FullRoomException, UserAlreadyLoggedException, BroadcastException;

    void userExit(Room room, ChatUser user) throws InvalidRoomException, BroadcastException;

    void broadcastMessage(Room room, ChatMessage message) throws BroadcastException;
}
