package hospital;

import java.util.Random;

public class Patient {

	static int INITIAL_HEALTH = 50;
	static int CURED = 100;
	static int DEAD = 0;
	static int TREATED_GAIN = 10;
	static int UNTREATED_LOSS = 1;

	static Random rand = new Random();

	String name;
	int health;
	int age;

	Patient() {
		age = rand.nextInt(69)+10;
		name = getName();
		health = INITIAL_HEALTH;
	}

	// check if patient is cured
	boolean cured() {
		return health==CURED;
	}

	// check if patient has died
	boolean died() {
		return health==DEAD;
	}

	// check if not already cured or dead
	boolean treatable() {
		return !(died() || cured());
	}

	// apply medicine
	void treat() {
		if(treatable()){
			health += TREATED_GAIN;
			if(health > CURED){
				health = CURED;
			}
		}

	}


	// reduce health if not treated in this step
	void untreated() {
		if(treatable()) {
			health -= UNTREATED_LOSS;
			if(health < DEAD) {
				health = DEAD;
			}
		}
	}

	// generate a random Krzmrgystani name
	String getName() {
		StringBuilder s = new StringBuilder();
		getOneName(s); // first name
		s.append(' ');
		getOneName(s); // last name
		return s.toString();
	}

	void getOneName(StringBuilder s) {
		s.append(Character.toUpperCase(consonant()));
		s.append(vowel());
		if (rand.nextInt(2) == 0) {
			s.append(consonant());
		}
		s.append(consonant());
		s.append(vowel());
		if (rand.nextInt(2) == 0) {
			s.append(consonant());
		}
		if (rand.nextInt(2) == 0) {
			s.append(consonant());
			s.append(vowel());
		}
	}

	// two ways to initialize a char array
	static char[] CONSONANTS = "bcdfghjklmnprstvz".toCharArray();
	static char[] VOWELS = { 'a', 'e', 'i', 'o', 'u' };

	char consonant() {
		return CONSONANTS[rand.nextInt(CONSONANTS.length)];
	}

	char vowel() {
		return VOWELS[rand.nextInt(VOWELS.length)];
	}

}
