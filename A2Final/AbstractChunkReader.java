package cipher;

import java.io.IOException;
import java.io.InputStream;

/**
 * A chunk reader that is used for reading input for all ciphers.
 */
public abstract class AbstractChunkReader implements ChunkReader{
   private int chunkSize;
   protected InputStream textData;
   protected byte[] chunk;

   /**
    * Construct an AbstractChunkReader with specified input and chunk size/
    *
    * @param in
    *          The InputStream the file to be encrypted or decrypted is on
    * @param size
    *          The size of each chunk
    */
   public AbstractChunkReader(InputStream in, int size) {
      textData = in;
      chunkSize = size;
   }

   @Override
   public int chunkSize() {
      return chunkSize;
   }

   @Override
   public boolean hasNext() {
      try {
         return (textData.available() > 0);
      } catch (IOException e) {
         System.out.println("the input text could not be read.");
      }
      return false;
   }

   @Override
   public abstract int nextChunk(byte[] data) throws java.io.EOFException, IOException;

   /**
    * Reset the chunk to make it be ready to store data when hasNext() is called next time.
    *
    * @return
    *        the reset chunk
    */
   public byte[] resetChunk(){
      for (int i = 0; i < chunk.length; i++){
         chunk[i] = 0;
      }
      return chunk;
   }
}
