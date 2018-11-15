package cipher;

import java.io.*;
import java.math.BigInteger;

/**
 * Command line interface to allow users to interact with your ciphers.
 * 
 * We have provided some infrastructure to parse most of the arguments. It is
 * your responsibility to implement the appropriate actions according to the
 * assignment specifications. You may choose to "fill in the blanks" or rewrite
 * this class.
 *
 * Regardless of which option you choose, remember to minimize repetitive code.
 * You are welcome to add additional methods or alter the provided code to
 * achieve this.
 *
 * 
 * @author Jarrett Coleman and William Li
 *
 */
public class Main {
	private Cipher cipherInUse;
	private OutputStream os;
	private InputStream is;
	private Boolean encrypt;
	private Boolean rsa = false;
	private Boolean rsaLoad = false;
	
	/**
	 * Runs the program
	 * @param args the command flags taken to be interpreted
	 */
	public static void main(String[] args) {
		Main run = new Main();
		run.parseOutputOptions(args, run.parseCipherFunction(args, run.parseCipherType(args, 0)));
	}

	/**
	 * Set up the cipher type based on the options found in args starting at
	 * position pos, and return the index into args just past any cipher type
	 * options.
	 * 
	 * @param args the array of command flags and given files and parameters for each command
	 * @param pos the position at which we want to start our analysis of args
	 * @return the position of the next item in args
	 * @throws IllegalArgumentException 
	 */
	private int parseCipherType(String[] args, int pos)
			throws IllegalArgumentException {
		// check if arguments are exhausted
		if (pos == args.length) return pos;
		CipherFactory factory = new CipherFactory();
		FileReader reader;
		BufferedReader bReader;

		String cmdFlag = args[pos++];
		switch (cmdFlag) {
		case "--caesar":
			/* finds the shift factor given after the "--caesar" command. Uses BigInteger so we don't import Integer and can also handle really big shift factors if necessary */
			BigInteger shift;
			try {
				shift = new BigInteger(args[pos]);
				cipherInUse = factory.getCaesarCipher(shift.intValue());
			}catch(NumberFormatException e) {
				System.out.println("You entered an invalid shift parameter. Terminating program.");
				System.exit(1);
			}

			return pos+1;

		case "--random":
			/* creates a random substitution cipher with a random key */
			cipherInUse = factory.getRandomSubstitutionCipher();
			return pos;
		case "--monoLoad":
			//load a monoaphabetic substitution cipher from a file

			try {
				reader = new FileReader(args[pos]);
				bReader = new BufferedReader(reader);
				String monoKey = bReader.readLine();
				String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
				if (monoKey.length() != 26){
					System.out.println("the key length for monoLoad should be 26. Terminating program.");
					System.exit(1);
				}
				else for (int i = 0; i < 26; i++){
					if (!monoKey.contains(alphabets.substring(i, i + 1))) {
						System.out.println("Not all alphabets are mapped. Terminating program.");
						System.exit(1);
					}
				}
				cipherInUse = factory.getMonoCipher(monoKey);
			} catch (FileNotFoundException e){
				System.out.println("The MonoCipher key file does not exist. Terminating program.");
				System.exit(1);
			} catch (IOException e1) {
				System.out.println("The MonoCipher key file could be read. Terminating program.");
				System.exit(1);
			}
			return pos + 1;
		case "--vigenere":
			//create a new Vigenere Cipher with the given key
			if (!args[pos].matches("^[A-Z]+$")){
				System.out.println("The Vignere key should be uppercase letters. Terminating program.");
				System.exit(1);
			}
			else {
				try {
					if (args[pos].getBytes("UTF-8").length > 128){
						System.out.println("The Vigenere key should not be longer than 128 bytes. Terminating program.");
						System.exit(1);
					}
				} catch (UnsupportedEncodingException e) {
					System.out.println("The string input to the commandline should be encoded in UTF-8. Terminating program.");
					System.exit(1);
				}
			}
			cipherInUse = factory.getVigenereCipher(args[pos]);
			return pos + 1;
		case "--vigenereLoad":
			//create a Vigenere cipher with key loaded from the given file

			try {
				reader = new FileReader(args[pos]);
				bReader = new BufferedReader(reader);
				String vigenereKey = bReader.readLine();
				if (!vigenereKey.matches("^[A-Z]+$"))
				{
					System.out.println("The Vigenere key should be uppercase letters. Terminating program.");
					System.exit(1);
				}
				else if (vigenereKey.getBytes("UTF-8").length > 128){
					System.out.println("The Vigenere key should not be longer than 128 bytes. Terminating program.");
					System.exit(1);
				}
				cipherInUse = factory.getVigenereCipher(vigenereKey);
			} catch (FileNotFoundException e){
				System.out.println("The Vignere key file does not exist. Terminating program.");
				System.exit(1);
			} catch (IOException e1) {
				System.out.println("The Vignere key file could be read. Terminating program.");
				System.exit(1);
			}
			return pos + 1;
		case "--rsa":
			cipherInUse = factory.getRSACipher();
			rsa = true;
			return pos;
		case "--rsaLoad":
			//load an RSA key from the given file

			try {
				reader = new FileReader(args[pos]);
				bReader = new BufferedReader(reader);
				BigInteger n = new BigInteger(bReader.readLine());
				BigInteger e = new BigInteger(bReader.readLine());
				BigInteger d = new BigInteger(bReader.readLine());
				cipherInUse = factory.getRSACipher(n, e, d);
			} catch (FileNotFoundException e){
				System.out.println("The rsa key file does not exist. Terminating program.");
				System.exit(1);
			} catch (IOException e1) {
				System.out.println("The rsa key file could be read. Terminating program.");
				System.exit(1);
			} catch (NumberFormatException e2){
				System.out.println("The key is not valid. Terminating program.");
				System.exit(1);
			}
			rsaLoad = true;
			rsa = true;
			return pos + 1;

		default:
			System.out.println("You entered an invalid command. Exiting program");
			System.exit(1);
		}
		return pos;
	}

	/**
	 * Parse the operations to be performed by the program from the command-line
	 * arguments in args starting at position pos. Return the index into args
	 * just past the parsed arguments.
	 * 
	 * @param args the array of command flags and given files and parameters for each command
	 * @param pos the position at which we want to start our analysis of args
	 * @return the position of the next item in args
	 * @throws IllegalArgumentException
	 */
	private int parseCipherFunction(String[] args, int pos)
			throws IllegalArgumentException {
		// check if arguments are exhausted
		if (pos == args.length) return pos;



		switch (args[pos++]) {
		case "--em":
			//encrypt the given string

			try {
				is = new ByteArrayInputStream(args[pos].getBytes("UTF-8"));
			} catch (IndexOutOfBoundsException e){
				System.out.println("Please provide String to be encrypted. Terminating Program.");
				System.exit(1);
			} catch (UnsupportedEncodingException e) {
				System.out.println("Only UTF-8 encoding is supported. Terminating program.");
				System.exit(1);
			}
			encrypt = true;
			return pos + 1;
		case "--ef":
			//encrypt the contents of the given file
			try {
				is = new FileInputStream(args[pos]);
			} catch (FileNotFoundException e) {
				System.out.println("The file to be encrypted could not be found. Terminating program.");
				System.exit(1);
			}
			encrypt = true;
			return pos + 1;
		case "--dm":
			//decrypt the given string -- substitution ciphers only
			if (rsa){
				System.out.println("RSA should not directly decrypt String. Terminating program.");
				System.exit(1);
			}
			try {
				is = new ByteArrayInputStream(args[pos].getBytes("UTF-8"));
			} catch (IndexOutOfBoundsException e){
				System.out.println("Please provide String to be decrypted.");
			} catch (UnsupportedEncodingException e) {
				System.out.println("Only UTF-8 encoding is supported. Terminating program.");
				System.exit(1);
			}
			encrypt = false;
			return pos + 1;
		case "--df":
			if (!rsaLoad){
				System.out.println("RSA with random generated keys should not be used to decrypt. Terminating program.");
				System.exit(1);
			}
			//decrypt the contents of the given file
			try {
				is = new FileInputStream(args[pos]);
			} catch (FileNotFoundException e) {
				System.out.println("The file to be decrypted could not be found. Terminating program.");
				System.exit(1);
			}
			encrypt = false;
			return pos + 1;
		default:
			System.out.println("You entered an invalid command. Terminating program");
			System.exit(1);
		}
		return pos;
	}

	/**
	 * Parse options for output, starting within {@code args} at index
	 * {@code argPos}. Return the index in args just past such options.
	 * 
	 * @param args the array of command flags and given files and parameters for each command
	 * @param pos the position at which we want to start our analysis of args
	 * @return the position of the next item in args
	 * @throws IllegalArgumentException
	 */
	private int parseOutputOptions(String[] args, int pos)
			throws IllegalArgumentException {
		// check if arguments are exhausted
		if (pos == args.length){
         System.out.println("Please provide an output option. Terminating program.");
         System.exit(1);
      }
		File f;
		FileOutputStream fos;
		String cmdFlag;
		while (pos < args.length) {
			switch (cmdFlag = args[pos++]) {
			case "--print":
				//print result of applying the cipher to the console -- substitution ciphers only
				os = System.out;
				try {
					if(encrypt) {
						if (rsa){
							System.out.println("RSA should not print encrypted message. Terminating program");
							System.exit(1);
						}
						cipherInUse.encrypt(is, os);
					}
					else {
						cipherInUse.decrypt(is, os);
					}
					is.close();
					os.close();
				} catch (IOException e) {
					System.out.println("The file to be encrypted or decrypted could not be read or "
							+ "encrypted or decrypted. Terminating program.");
					System.exit(1);
				}

				break;

			case "--out":
				//output result of applying the cipher to a file

				if (args.length <= pos) {
					System.out.println("Please provide a file to be saved. Terminating program.");
					System.exit(1);
				}

				if (args[pos].equals("--save")) {
					System.out.println("Please provide a file to be saved. Terminating program.");
					System.exit(1);
				}

				try {
					os = new FileOutputStream(args[pos]);
					if(encrypt) {
						cipherInUse.encrypt(is, os);
					}
					else {
						cipherInUse.decrypt(is, os);
					}
					is.close();
					os.close();
				} catch (IOException e) {
					System.out.println("The file to be encrypted or decrypted could not be read or encrypted or decrypted. "
							+ "Or the output file could not be written. Terminating program.");
					System.exit(1);
				}
				pos++;
				break;

			case "--save":
				//save the cipher key to a file

				try {
					f = new File(args[pos]);
					fos = new FileOutputStream(f);
					cipherInUse.save(fos);
					fos.close();
				} catch (IOException e) {
					System.out.println("The file to be saved could not be read. Terminating program.");
					System.exit(1);
				}
				pos++;
				break;
			default:
				System.out.println("You entered an invalid command. Exiting program");
				System.exit(1);
			}
		}
		return pos;
	}

}
