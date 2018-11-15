package cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Random;
/**
 * RSA Cipher using Euler's totient function and modular arithmetic
 * 
 * @author Jarrett Coleman and William Li
 *
 */
public class RSACipher extends AbstractCipher{
	private BigInteger n;
	private BigInteger d;
	private BigInteger e;
	private BigInteger totient;
	private BigInteger primeP;
	private BigInteger primeQ;
	private BigInteger s;
	private BigInteger c;
	
	/**
	 * Creates an instance of RSACipher that sets the value of n, e and d
	 * @param nGiven the modulus to be used
	 * @param eGiven the public key to be used
	 * @param dGiven the private key to be used
	 */
	public RSACipher(BigInteger nGiven, BigInteger eGiven, BigInteger dGiven) {
		n = nGiven;
		e = eGiven;
		d = dGiven;
	}
	
	/**
	 * Creates an instance of RSACipher with random modulus and keys
	 */
	public RSACipher() {
		createRandomTotient();
		createRandomModulus();
		createRandomPublicKey();
		createPrivateKey();
	}

	/**
	 * Chooses a random totient by multiplying two (probably) prime numbers that total up to less than 1023 bits
	 */
	public void createRandomTotient() {
		primeP = new BigInteger(511, 20, new Random());
		primeQ = new BigInteger(511, 20, new Random());
		totient = primeP.add(BigInteger.ONE.negate()).multiply(primeQ.add(BigInteger.ONE.negate()));
	}
	
	/**
	 * Uses the random prime numbers chosen earlier to create a random modulus
	 */
	public void createRandomModulus() {
		n = primeP.multiply(primeQ);
	}
	
	/**
	 * Chooses a random public key less than the size of the totient, chooses a new one if it is not relatively prime to the totient.
	 */
	public void createRandomPublicKey() {
		e = new BigInteger(totient.bitLength() - 1, new Random());
		while(!e.gcd(totient).equals(BigInteger.ONE)) {
			e = new BigInteger(totient.bitLength() - 1, new Random());
		}
	}
	
	/**
	 * Uses the public key and the totient to create the private key
	 */
	public void createPrivateKey() {
		d = e.modInverse(totient);
	}
	
	@Override
	public void encrypt(InputStream in, OutputStream out) {
		RSAEncryptionChunkReader chunkIn = new RSAEncryptionChunkReader(in, 126);
		try {
			while (chunkIn.hasNext()){
				chunkIn.nextChunk(chunkIn.chunk);
				s = new BigInteger(chunkIn.chunk);
				BigInteger encryptedBytes = s.modPow(e, n);
				byte[] byteArrayEncrypt = encryptedBytes.toByteArray();
				byte[] byteArrayPadding = new byte[128];
				int encryptedLength = byteArrayEncrypt.length;
				for (int i = 0; i < 128 - encryptedLength; i++){
					byteArrayPadding[i] = 0;
				}
				for (int i = 0; i < encryptedLength; i++){
					byteArrayPadding[i + 128 - encryptedLength] = byteArrayEncrypt[i];
				}
				out.write(byteArrayPadding);
			}
			} catch (IOException e) {
			System.out.print("File could not be read.");
		}
	}

	@Override
	public void decrypt(InputStream in, OutputStream out) {
		GeneralChunkReader chunkIn = new GeneralChunkReader(in, 128);
		try {
			while (chunkIn.hasNext()){
				chunkIn.nextChunk(chunkIn.chunk);
				c = new BigInteger(chunkIn.chunk);
				BigInteger decryptedBytes = c.modPow(d, n);
				byte[] byteArrayDecrypt = decryptedBytes.toByteArray();
				int plainLength = byteArrayDecrypt[0];
				byte[] plainBytes = new byte[plainLength];
				for (int i = 1; i <= plainLength; i++){
					plainBytes[i - 1] = byteArrayDecrypt[i];
				}
				out.write(plainBytes);
			}
		} catch (IOException e) {
			System.out.print("File could not be read.");
		}
	}

	@Override
	public void save(OutputStream out){
		try {
			out.write(n.toString().getBytes());
			out.write("\r\n".getBytes());
			out.write(e.toString().getBytes());
			out.write("\r\n".getBytes());
			out.write(d.toString().getBytes());
			out.write("\r\n".getBytes());
		} catch(IOException e){
			System.out.println("File could not be written.");
		}
	}
	
}
