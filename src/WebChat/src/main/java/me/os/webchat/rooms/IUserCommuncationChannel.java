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
public interface IUserCommuncationChannel {
    void send(ChatMessage message) throws BroadcastException;
    
    boolean isActive();
}
