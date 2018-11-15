package cipher;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * A place to put some inherited code for all cipher types
 */
public abstract class AbstractCipher implements Cipher {
	static Scanner sysin = new Scanner(System.in);
	protected String key = "";
	protected int shiftKey;
	
	@Override
	public void encrypt(InputStream in, OutputStream out){
		String plaintext;
		GeneralChunkReader chunkIn = new GeneralChunkReader(in, 128);
		try{
			while (chunkIn.hasNext()){
				chunkIn.nextChunk(chunkIn.chunk);
				plaintext = new String(chunkIn.chunk, StandardCharsets.UTF_8);
				String ciphertext = encrypt(plaintext);
				byte[] textBytes = ciphertext.getBytes(StandardCharsets.UTF_8);
				out.write(textBytes, 0, textBytes.length);
			}

		} catch (IOException e){
			System.out.println("File could not be read or written");
		}
	}


	@Override
	public void decrypt(InputStream in, OutputStream out){
		String ciphertext;
		GeneralChunkReader chunkIn = new GeneralChunkReader(in, 128);
		try {
			while (chunkIn.hasNext()){
				int chunkLen = chunkIn.nextChunk(chunkIn.chunk);
				ciphertext = new String(chunkIn.chunk, StandardCharsets.UTF_8).substring(0, chunkLen);
				String plaintext = decrypt(ciphertext);
				byte[] textBytes = plaintext.getBytes(StandardCharsets.UTF_8);
				int textLength = textBytes.length;
				out.write(textBytes, 0, textLength);
			}
		} catch (IOException e){
			System.out.println("File could not be read or written.");
		}
	}

	@Override
	public void save(OutputStream out) throws IOException{
		try {
			byte keyBytes[] = key.getBytes(StandardCharsets.UTF_8);
			int keyLength = keyBytes.length;
			out.write(keyBytes, 0, keyLength);
			out.write("\r\n".getBytes());
		} catch(IOException e){
			System.out.println("File could not be written.");
		}
	}

	@Override
	public String encrypt(String plaintext) {
		return "";
	}

	@Override
	public String decrypt(String ciphertext) {
		return "";
	}

	@Override
	public void reset() {
		
	}
	
	
	/**
	 * Maps any given string to a new string in standard representation, meaning with only whitespace and upper case characters.
	 * Lower case characters are converted to upper case.
	 * @param input The string to be made into standard representation
	 * @return The new string in standard representation
	 */
	public String toStandardRepresentation(String input) {
		String stringBuilder = "";
		for(int i = 0; i<input.length(); i++) {
			int charEncoding = (int)input.charAt(i);
			if((charEncoding <= 90 && charEncoding >= 65) || charEncoding == 32 || charEncoding == 9 || charEncoding == 10 || charEncoding == 13) {
				stringBuilder = stringBuilder + (char)charEncoding;
			}
			else if(charEncoding <= 122 && charEncoding >= 97) {
				stringBuilder = stringBuilder + (char)(charEncoding - 32);
			}
		}
		return stringBuilder;
	}
}

