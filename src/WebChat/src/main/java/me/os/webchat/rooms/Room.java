package me.os.webchat.rooms;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private final int MaxUsersPerRoom = 10;

    public Room(int id, String name) {
        super();
        this.id = id;
        this.name = name;
        this.loggedUsers = new ArrayList<>();
    }

    private int id;
    private String name;
    private List<ChatUser> loggedUsers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChatUser> getLoggedUsers() {
        return loggedUsers;
    }

    public void joinUser(ChatUser user) throws UserAlreadyLoggedException, FullRoomException {

        if (getLoggedUsers().size() >= MaxUsersPerRoom) {
            throw new FullRoomException();
        }

        if (isUserPresent(user)) {
            throw new UserAlreadyLoggedException();
        }

        this.loggedUsers.add(user);
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
