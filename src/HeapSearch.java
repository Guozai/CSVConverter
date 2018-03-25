import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class HeapSearch {
    private final int INT_SIZE = 4;
    private final int HEADER_SIZE = 1; // each element has a 1 byte header saving the length of the element
    private final int REG_NAME_SIZE = 20;
    private String queryKey;
    private int pageSize;

    private int pos = 0; // position pointer
    private int numRec = 0; // number of records in the file
    private int numPage = 0;

    public HeapSearch (String queryKey, int pageSize) {
        this.queryKey = queryKey;
        this.pageSize = pageSize;
    }

    public void launch() {
        int lenStr = 0;

        // read the source file
        //try (FileInputStream fis = new FileInputStream(new File("heap." + Integer.toString(pageSize)))) {
        try (FileInputStream fis = new FileInputStream(new File("heap.64"))) {
            // allocate a channel to read the file
            FileChannel fc = fis.getChannel();
            // allocate a buffer, size of pageSize
            ByteBuffer buffer = ByteBuffer.allocate(pageSize);
            // read a page of pageSize bytes
            // -1 means eof.
            if (fc.read(buffer) != -1) {
                // accumulate the number of page after read(buf)
                numPage++;
                // flip from filling to emptying
                buffer.flip();

                // get the first 4 bytes of page 1
                if (numPage == 1) {
                    numRec = buffer.getInt();
                    pos += INT_SIZE;
                }

                // moving pointer to the beginning of BN_NAME (col 2)
                pos += HEADER_SIZE + REG_NAME_SIZE;

                // get the length of data element
                try {
                    lenStr = buffer.get(pos) & 0xff;
                    pos += HEADER_SIZE;
                } catch (NumberFormatException e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }

                // get BN_NAME
                byte[] bnName = new byte[lenStr];
                for (int i = 0; i < 8; i++) {
                    bnName[i] = buffer.get(pos);
                    pos++;
                }
                String s = new String(bnName, "US-ASCII");
                System.out.println(s);

//                      for (int i = 0; i < 3; i++) {
//                    char c = buffer.getChar();
//                    System.out.print(c);
//                    System.out.print(", ");
//                }

                buffer.clear();
            }

//                buf.flip();
//                numRec = buf.getInt();
//                buf = null;

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
