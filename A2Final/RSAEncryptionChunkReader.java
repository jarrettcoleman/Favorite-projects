package cipher;

import java.io.InputStream;
import java.io.IOException;

/**
 * A chunk reader that is used for reading input when doing RSA encryption.
 */
public class RSAEncryptionChunkReader extends AbstractChunkReader {

   /**
    * Create a instance of RSAEncryptionChunkReader with specified input file and chunk size.
    *
    * @param in
    *          The InputStream the file to be encrypted is on
    * @param size
    *           The size of each chunk
    */
   public RSAEncryptionChunkReader(InputStream in, int size){
      super(in, size);
      chunk = new byte[chunkSize() + 1];
   }


   @Override
   public int nextChunk(byte[] data) throws java.io.EOFException, IOException{
      resetChunk();
      int count = textData.read(data, 1, chunkSize());
      data[0] = (byte)(count);
      return count;
   }


}
