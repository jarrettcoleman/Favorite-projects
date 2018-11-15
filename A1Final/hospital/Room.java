package hospital;

public class Room {

	static int ROOMS = 10;
	static int nextRoomNumber;
	int roomNumber;
	Room next, prev;
	Patient patient;

	Room(Room prev) {
		roomNumber = nextRoomNumber;
		patient = new Patient();
		this.prev = prev;
		nextRoomNumber++;
	}

	// create the rooms, one patient per room
	static Room createRooms() {
		nextRoomNumber = 100;
		Room[] roomArr = new Room[ROOMS];
		roomArr[0] = new Room(roomArr[0]);
		for(int i = 1; i < ROOMS; i++) {
			roomArr[i] = new Room(roomArr[i-1]);
			roomArr[i-1].next = roomArr[i];
		}
		//sets first room's prev to last room, last roomm's next to first room (first room's prev is already itself by default
		if(ROOMS == 1) {
			roomArr[0].next = roomArr[0];
		}
		else {
			roomArr[0].prev = roomArr[ROOMS-1];
			roomArr[ROOMS-1].next = roomArr[0];
		}
		return (roomArr[0]);
	}
}
