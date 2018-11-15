package cipher;

public class CaesarCipher extends MonoCipher {

	
	
	/**
	 * Creates a new CaesarCipher
	 * @param shift provides the character shift to be used by this CaesarCipher
	 */
	public CaesarCipher(int shift) {
		super("");
		if(shift >= 0) this.shiftKey = shift % 26;
		
		else this.shiftKey = 26 - (-shift) % 26;
		
		for(int i = 0; i < 26; i++) {
			//each element of keyArray represents the corresponding letter of the alphabet. This stores a character encoding for each uppercase letter of the alphabet shifted by shiftKey
			keyArray[i] = (char)(65 + (i + shiftKey) % 26);
		}
		key = new String(keyArray);
	}
}
