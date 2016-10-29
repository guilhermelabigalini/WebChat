package me.os.webchat.rooms;

import java.util.ArrayList;
import java.util.List;

public class RoomState {

    private List<ChatUser> loggedUsers;

    public RoomState() {
        this.loggedUsers = new ArrayList<>();
    }

    public List<ChatUser> getLoggedUsers() {
        return loggedUsers;
    }

    public void setLoggedUsers(List<ChatUser> loggedUsers) {
        this.loggedUsers = loggedUsers;
    }

    public boolean isUserPresent(ChatUser cu) {
        return this.loggedUsers.stream().anyMatch(u -> u.getDisplayName().equals(cu.getDisplayName()));
    }

    public void removeUser(String userId) {
        this.loggedUsers.removeIf(u -> u.getDisplayName().equals(userId));
    }
}
