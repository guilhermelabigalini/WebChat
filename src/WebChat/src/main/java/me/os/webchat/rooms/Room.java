package me.os.webchat.rooms;

public class Room {

	public Room(int id, String name) {
		super();
		this.id = id;
		this.name = name;
		this.roomState = new RoomState();
	}

	private int id;
	
	private String name;
	
	private RoomState roomState;

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

	public RoomState getRoomState() {
		return roomState;
	}

	public void setRoomState(RoomState loggedUsers) {
		this.roomState = loggedUsers;
	}
	
}
