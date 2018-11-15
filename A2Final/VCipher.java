package cipher;

/**
 * Viginere Cipher class for encrypting and decrypting acording to a viginere cipher
 * @author Jarrett Coleman and William Li
 *
 */
public class VCipher extends AbstractCipher{
	private int keyCounter = 0;

	/**
	 * Constructs an instance of VCipher
	 * @param keyString The repeating key to be used to encrypt or decrypt ciphertext
	 */
	public VCipher(String keyString) {
		key = keyString;
	}

	/**
	 * Changes the character of the key being used to translate the ciphertext or plaintext
	 */
	public void changeShift() {
		keyCounter = (keyCounter+1)%(key.length());
		shiftKey = ((int)key.charAt(keyCounter)-64);
	}

	@Override
	public String encrypt(String rawPlaintext) {
		String messageBuilder = "";
		String plaintext = toStandardRepresentation(rawPlaintext);
		reset();
		for(int i = 0; i < plaintext.length();i++) {
			int charEncoding = (int)plaintext.charAt(i);

			if(charEncoding==32 ||charEncoding == 9 || charEncoding == 10 || charEncoding == 13) {
				messageBuilder = messageBuilder + (char)charEncoding; 
			}
			else {
				//uses UTF-8 character encoding to shift alphabetic characters, looping back to beginning of alphabet if it goes over the end
				messageBuilder = messageBuilder + (char)((charEncoding + shiftKey-65)%26+65);
				//here the change shift switches the pointer in VCipher because that key shift has been used, while if there had been a blank space no shift would have been used.
				changeShift();
			}
		}
		return messageBuilder;
	}
	
	@Override
	public String decrypt(String ciphertext) {
		String messageBuilder = "";
		reset();
		for(int i = 0; i < ciphertext.length();i++) {
			int charEncoding = (int)ciphertext.charAt(i);

			if(charEncoding==32 ||charEncoding == 9 || charEncoding == 10 || charEncoding == 13) {
				messageBuilder = messageBuilder + (char)charEncoding;
			}
			else {
				//added 26 to avoid improper evaluation of negative numbers, shifted down by 65 to keep within mod 26, reshifted back after modulus operation
				messageBuilder = messageBuilder + (char)((charEncoding - 65 - shiftKey + 26)%26+65);

				//here the changeshift acts similarly to the encrypt changeShift, giving the next key shift from the VCipher and RandomCipher to undo the operation
				//in the same order as encryption, while CaesarCipher does nothing because all the shifts are the same.
				changeShift();
			}
		}
		return messageBuilder;
	}
	
	@Override
	public void reset() {
		shiftKey = ((int)key.charAt(keyCounter)-64);
	}

}