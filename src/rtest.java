import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class rtest {
    private final int INT_SIZE = 4;
    private final int HEADER_SIZE = 1; // each element has a 1 byte header saving the length of the element
    private String queryKey;
    private int pageSize;

    private int pos = 0; // position pointer
    private int numRec = 0; // number of records in the file
    private int numPage = 0;
    private byte[] page = new byte[pageSize];

    public rtest (String queryKey, int pageSize) {
        this.queryKey = queryKey;
        this.pageSize = pageSize;
    }

    public void launch() {
        // read the source file
        try (FileInputStream fis = new FileInputStream(new File("heap." + Integer.toString(pageSize)))) {
            // allocate a channel to read the file
            FileChannel fc = fis.getChannel();

            // allocate a buffer, size of pageSize
            ByteBuffer buffer = ByteBuffer.allocate(pageSize);
//            showStats( "newly allocated read", fc, buffer );

            // read a chunk of raw bytes, up to 15K bytes long
            // -1 means eof.
            int bytesRead = fc.read( buffer );
//            showStats( "after first read", fc, buffer );
//
//            // flip from filling to emptying
//            showStats( "before flip", fc, buffer );
            buffer.flip();
//            showStats( "after flip", fc, buffer );
            byte[] receive = new byte[ 4 ];
            buffer.get( receive );
            showStats( "after first get", fc, buffer );
            buffer.get( receive );
            showStats( "after second get", fc, buffer );
            // empty buffer to fill with more data.
            buffer.clear();
            showStats( "after clear", fc, buffer );
            bytesRead = fc.read( buffer );
            showStats( "after second read", fc, buffer );
            // flip from filling to emptying
            showStats( "before flip", fc, buffer );
            buffer.flip();
            showStats( "after flip", fc, buffer );

//            if (fc.read(buf) != -1) {
//                System.out.println(fc.size());
//                buf.flip();
//                numRec = buf.getInt();
//                buf = null;
//            }

//            buf = ByteBuffer.wrap(page);
//            byte lenStr = buf.get(4);
//            System.out.println((int)lenStr);

            // get the number of pages need to read to the end of the binary file
//            numPage = in.available()/pageSize;
            // read in a page
//            for (int i = 0, lenFile = numPage; i < lenFile; i++) {
//                in.readFully(page);
//                if (i == 0) {
            // read the number of records in the file
//                    numRec = in.readInt();
//                    pos += INT_SIZE;
//                    System.out.println("numRec: " + numRec);
//                }
//            in.readInt();
//                int lenStr = (int)page[0];
//                pos += HEADER_SIZE;
//                System.out.println("lenStr: " + lenStr);
//            }

            fc.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void showStats(String where, FileChannel fc, Buffer b) throws IOException {
        System.out.println( where +
                " channelPosition: " +
                fc.position() +
                " bufferPosition: " +
                b.position() +
                " limit: " +
                b.limit() +
                " remaining: " +
                b.remaining() +
                " capacity: " +
                b.capacity() );
    }
}
