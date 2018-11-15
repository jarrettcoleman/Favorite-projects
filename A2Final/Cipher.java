package cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



/**
 * A cipher that both encrypts and decrypts.
 */
public interface Cipher {

   /**
    * Encrypts a message from the input stream using this cipher and sends the
    * result to the output stream.
    * 
    * @param in
    *           The InputStream the plaintext is on
    * @param out
    *           The OutputStream to send the ciphertext to
    * @throws IOException
    */
   public void encrypt(InputStream in, OutputStream out) throws IOException;

   /**
    * Decrypts a message from the input stream according to this cipher's
    * decryption protocol and sends the result to the output stream.
    * 
    * @param in
    *           The InputStream the ciphertext is on
    * @param out
    *           The OutputStream to send the plaintext to
    * @throws IOException
    */
   public void decrypt(InputStream in, OutputStream out) throws IOException;

   /**
    * Encrypts the plaintext string {@code plaintext} and returns the ciphertext
    * as a {@code String}. Makes sense only for alphabetic ciphers.
    * 
    * @param plaintext
    *           The plaintext to be encrypted
    * @return An encrypted ciphertext
    */
   public String encrypt(String plaintext);

   /**
    * Decrypts the ciphertext string {@code ciphertext} and returns the plaintext
    * as a {@code String}. Makes sense only for alphabetic ciphers.
    * 
    * @param ciphertext
    *           The ciphertext to decrypt
    * @return The decrypted plaintext
    */
   public String decrypt(String ciphertext);
   
   /**
    * Resets the cipher before beginning to encrypt or decrypt a new message.
    */
   default public void reset() {
      // default is not to do anything
   }

   /**
    * Writes the cipher key to an OutputStream
    * 
    * @param out
    *           The OutputStream to write the cipher key to
    */
   public void save(OutputStream out) throws IOException;

}
