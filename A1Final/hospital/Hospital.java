package hospital;

import java.util.Scanner;

public class Hospital {

	Room firstRoom;
	Doctor doctor;
	private boolean quit = false;
	public static void main(String[] args) {
		// TODO: implement me
		Hospital start = new Hospital();
		start.play();
	}

	Hospital() {
		firstRoom = Room.createRooms();
		doctor = new Doctor(firstRoom);
	}

	void play() {
		System.out.println("\n*******************************************\n" +
				"* Welcome to Krzmrgystan General Hospital *\n" +
				"*******************************************\n\n" +
				"Type 'h' for instructions");

		final Scanner sysin = new Scanner(System.in);

		// read-eval-print loop
		while(!done()){
			displayStatus();
			System.out.println("The doctor is in room " + doctor.location.roomNumber + " with " + doctor.medicine + " medicine left."
					+ "\nplease enter a command: t = treat, n = next room, p = previous room, h = help, q = quit");
			processCommand(sysin.nextLine());
		}
		displayStatus();
		System.out.println("Patients cured: " + cured());
		System.out.println("Medicine remaining: " + doctor.medicine);
	}

	void processCommand(String cmd) {
		for(int i = 0; i < cmd.length(); i++) {
			switch (cmd.charAt(i)){
			case 't': treat(); break;
			case 'q': quit(); break;
			case 'n': move('n'); break;
			case 'p': move('p'); break;
			case 'h': displayHelp(); break;
			default: System.out.println("Invalid Command. Skipped commmand \"" + cmd.charAt(i) + "\""); break;
			}
			if(done()) break;
			
		}
	}

	// quit
	void quit() {
		quit = true;
	}

	// apply medicines
	void treat() {
		// current patient gains health
		doctor.location.patient.treat();
		//all other patients lose health
		Room indexRoom = doctor.location.next;
		while(!indexRoom.equals(doctor.location)) {
			indexRoom.patient.untreated();
			indexRoom = indexRoom.next;
		}
		doctor.useMedicine();
	}

	// move forward if ch == 'n', backward if ch == 'p'
	void move(char ch) {
		//only 'n' and 'p' will be passed because of the way it is implemented in processCommand
		if(ch == 'n') doctor.location = doctor.location.next;
		else if(ch == 'p') doctor.location = doctor.location.prev;
		//all patients lose health
		Room indexRoom = firstRoom;
		do {
			indexRoom.patient.untreated();
			indexRoom = indexRoom.next;
		}while(!indexRoom.equals(firstRoom));
	}

	// count the number of cured patients
	int cured() {
		Room indexRoom = firstRoom;
		int counter = 0;
		do {
			if(indexRoom.patient.cured()) counter++;
			indexRoom = indexRoom.next;
		}while(indexRoom != firstRoom);
		return counter;
	}

	// check if game is over
	boolean done() {
		//counts number of patients cured or dead
		Room indexRoom = firstRoom;
		int counter = 0;
		do{
			if(!indexRoom.patient.treatable()) counter++;
			indexRoom = indexRoom.next;
		} while(indexRoom != firstRoom);

		return counter == Room.ROOMS|| quit == true ||doctor.medicine == 0;
	}

	void displayStatus() {
		System.out.format("\n%-4s  %-17s  %-3s  %s\n", "Room", "Patient", "Age", "Health");
		Room room = firstRoom;
		do {
			System.out.format("%3d   %-17s  %2d   %d", room.roomNumber,
					room.patient.name, room.patient.age, room.patient.health);
			if (room.patient.cured()) System.out.print(" recovered!");
			if (room.patient.died()) System.out.print(" died!");
			System.out.println();
			room = room.next;
		} while (room != firstRoom);
	}

	void displayHelp() {
		final String helpFormat =
				"-------------------------------------------------------------\n" +
						"Krzmrgystan General Hospital Help\n\n" +
						"There are %d rooms arranged in a ring, each with 1 patient.\n\n" +
						"Enter a command at the prompt. The commands are:\n" +
						"  n  move to the next (higher-numbered) room\n" +
						"  p  move to the previous (lower-numbered) room\n" +
						"  t  treat the patient in this room\n" +
						"  h  display this help screen\n" +
						"  q  quit\n" +
						"You can enter a sequence of commands on the same line,\n" +
						"  like this: ttttn\n\n" +
						"Treating a patient uses one unit of medicine, after which the\n" +
						"  treated patient's health improves by %d.\n" +
						"Treating a patient or moving takes one unit of time, during\n" +
						"  which all untreated patients' health deteriorates by %d.\n" +
						"A patient is cured when their health reaches %d.\n" +
						"A patient dies when their health reaches %d.\n" +
						"How many patients can you save?\n" +
						"-------------------------------------------------------------\n";

		System.out.format(helpFormat, Room.ROOMS, Patient.TREATED_GAIN,
				Patient.UNTREATED_LOSS, Patient.CURED, Patient.DEAD);
	}
}
