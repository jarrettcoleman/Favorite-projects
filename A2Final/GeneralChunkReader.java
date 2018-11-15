package cipher;

import java.io.InputStream;
import java.io.IOException;

/**
 *  A chunk reader that is used for reading input for substitution ciphers and when doing RSA decryption.
 */
public class GeneralChunkReader extends AbstractChunkReader {

   /**
    * Create a instance of GeneralChunkReader with specified input file and chunk size.
    *
    * @param in
    *          The InputStream the file to be encrypted or decrypted is on
    * @param size
    *          The size of each chunk
    */
   public GeneralChunkReader(InputStream in, int size){
      super(in, size);
      chunk = new byte[chunkSize()];
   }

   @Override
   public int nextChunk(byte[] data) throws java.io.EOFException, IOException {
      resetChunk();
      int count = textData.read(data, 0, chunkSize());
      return count;
   }
}