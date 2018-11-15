package cipher;

/**
 * Monoalphabetic Cipher class for decrypting Caesar Ciphers and Random alphabetic substitution Ciphers
 * @author Jarrett Coleman
 *
 */
public class MonoCipher extends AbstractCipher {
	protected char[] keyArray = new char[26];
	
	/**
	 * Creates an instance of MonoCipher for decryption of CaesarCiphers or RandomCiphers
	 * @param decryptString the key to be used in decryption
	 */
	public MonoCipher(String decryptString) {
		key = decryptString;
	}
	
	@Override
	public String encrypt(String rawPlaintext) {
		String messageBuilder = "";
		String plaintext = toStandardRepresentation(rawPlaintext);
		
		for(int i = 0; i < plaintext.length();i++) {
			int charEncoding = (int)plaintext.charAt(i);

			if(charEncoding==32 ||charEncoding == 9 || charEncoding == 10 || charEncoding == 13) {
				messageBuilder = messageBuilder + (char)charEncoding; 		
			}
			else {
				//uses UTF-8 character encoding to shift alphabetic characters, looping back to beginning of alphabet if it goes over the end
				messageBuilder = messageBuilder + key.charAt(charEncoding-65);		
			}
		}
		return messageBuilder;
	}

	@Override
	public String decrypt(String ciphertext) {
		String messageBuilder = "";
		for(int i = 0; i < ciphertext.length();i++) {
			int charEncoding = (int)ciphertext.charAt(i);

			if(charEncoding==32 ||charEncoding == 9 || charEncoding == 10 || charEncoding == 13) {
				messageBuilder = messageBuilder + (char)charEncoding;
			}
			else {
				messageBuilder = messageBuilder + (char)(key.indexOf(charEncoding) + 65);
			}
		}
		return messageBuilder;
	}
}
