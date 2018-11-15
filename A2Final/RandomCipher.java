package cipher;


import java.util.Random;

/**
 * Random alphabetic substitution cipher maps each element of the alphabet to another random letter
 * @author Jarrett Coleman and William Li
 *
 */
public class RandomCipher extends MonoCipher{
	private String randomKey = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private Random rand = new Random();
	
	/**
	 * Constructs an instance of RandomCipher that randomly creates an alphabetic key
	 */
	public RandomCipher(){
		super("");
		int randomIndex;
		for(int i = 0; i < 26; i++) {
			randomIndex = rand.nextInt(26-i);
			keyArray[i] = randomKey.charAt(randomIndex);
			randomKey = randomKey.substring(0, randomIndex) + randomKey.substring(randomIndex+1);
		}
		key = new String(keyArray);
	}
	
	

	
}
