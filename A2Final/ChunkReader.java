package cipher;

import java.io.EOFException;
import java.io.IOException;

/**
 * A ChunkReader reads bytes from an input stream in chunks of up to some fixed
 * number of bytes.
 */
public interface ChunkReader {

   /**
    * Returns the maximum number of bytes in a chunk.
    */
   public int chunkSize();

   /**
    * Returns true if there is at least one more byte of data to be read in the
    * current stream.
    */
   public boolean hasNext();

   /**
    * Returns the next chunk of up to {@code chunkSize()} bytes from the current
    * input stream. The returned bytes are placed in the array {@code data}. The
    * number of bytes returned is always {@code chunkSize()}, unless the end of
    * the input stream has been reached and there are fewer than
    * {@code chunkSize()} bytes available, in which case all remaining bytes are
    * returned.
    * 
    * @param data
    *           an array of length at least {@code chunkSize()}.
    * @return The number of bytes returned, which is always between 1 and the
    *         chunk size.
    * @throws EOFException
    *            if there are no more bytes available.
    * @throws IOException
    */
   public int nextChunk(byte[] data) throws java.io.EOFException, IOException;
}
