package hospital;

public class Doctor {

	static int MEDICINE = 120; // initial quantity
	static int DOSAGE = 1; // enough for one treatment

	int medicine = MEDICINE;
	Room location;

	Doctor(Room room) {
		location = room;
	}

	// decrease the amount of medicine remaining by DOSAGE
	void useMedicine() {
		medicine-=DOSAGE;
	}

	// check if enough medicine remaining for one dose
	boolean medicineLeft() {
		return medicine>=DOSAGE;
	}
}
