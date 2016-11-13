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
public class ChatMessage {

    public final static String MESSAGETYPE_MESSAGE = "message";
    public final static String MESSAGETYPE_JOINED = "joined";
    public final static String MESSAGETYPE_LEAVE = "leave";
    public final static String MESSAGETYPE_USERLIST = "userlist";

    private int room;
    private String from;
    private String to;
    private String type;
    private String body;
    private String destination;
    private boolean reserved;

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }
    
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

}
