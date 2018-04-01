import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class HeapSearch {
    private final int INT_SIZE = 4;
    private final int LEN_STR_SIZE = 1; // each element has a 1 byte header saving the length of the element
    private final int REG_NAME_SIZE = 20;
    private final int BN_NAME_SIZE = 200;
    private final int STATUS_SIZE = 15;
    private final int DATE_SIZE = INT_SIZE * 3;
    private final int STATE_NUM_SIZE = 20;
    private final int STATE_SIZE = 3;
    private final int ABN_SIZE = 13;
    private final int COMMA_SIZE = 1;
    private final int RECORD_SIZE = REG_NAME_SIZE + INT_SIZE + BN_NAME_SIZE + STATUS_SIZE + 3 * DATE_SIZE + STATE_NUM_SIZE + STATE_SIZE + ABN_SIZE + COMMA_SIZE;
    private int NUM_RECORDS_PER_PAGE;

    private String queryKey;
    private int pageSize;
    private String fileName;

    private int REC_NUM = 0;

    private int lenStr = 0;
    private int pos = 0; // position pointer
    private int pcol = 0;
    private boolean isEndPage = false;
    private boolean isEndFile = false;
    private int numRec = 0; // number of records in the file
    private int numPage = 0;
    // count of record searched
    private int countRec = 0;
    private int countFound = 0;

    public HeapSearch (String queryKey, int pageSize) {
        this.queryKey = queryKey;
        this.pageSize = pageSize;
        fileName = "heap." + Integer.toString(pageSize);
        NUM_RECORDS_PER_PAGE = Math.floorDiv(pageSize, RECORD_SIZE);
    }

    public void launch() {
        // read the source file
        try (FileInputStream fis = new FileInputStream(new File(fileName))) {
            // allocate a channel to read the file
            FileChannel fc = fis.getChannel();
            // allocate a buffer, size of pageSize
            ByteBuffer buffer = ByteBuffer.allocate(pageSize);
            // read a page of pageSize bytes; -1 means eof.
            while (fc.read(buffer) != -1) {
                // flip from filling to emptying
                buffer.flip();
                // reset pointer
                pos = 0;
                pos += REG_NAME_SIZE;

                for (int i = 0; i < NUM_RECORDS_PER_PAGE; i++) {
                    // business name
                    byte[] temp = new byte[BN_NAME_SIZE];
//                    buffer.get(temp, pos, BN_NAME_SIZE);
//                    String s = new String(temp, "US-ASCII");
//                    System.out.println(s);
                    // go through the rest of columns of the record
                    pos += RECORD_SIZE;
                }
                buffer.clear();
                // numRec increment
                numRec++;
                System.out.println("Reach end of page");
            }

            System.out.println("Reach end of file");

            fc.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        if (countFound == 0)
            System.out.println("BN_Name does not contain " + queryKey + ".");
        else
            System.out.println(countFound + " records found containing " + queryKey + "!");
    }

    private int getLenStr (ByteBuffer buffer) {
        try {
            lenStr = (int)buffer.get(pos);
            if (lenStr == -1) // found null element
                lenStr = 0;
        } catch (NumberFormatException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return lenStr;
    }

    private boolean checkEOPmark(ByteBuffer buffer) {
        byte[] bnName = new byte[LEN_STR_SIZE];
        bnName[0] = buffer.get(pos);
        String s = "";
        try {
            s = new String(bnName, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (s.equals("}")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkNotNull (ByteBuffer buffer) { return false; }

    private String getString(ByteBuffer buffer) {
        int pointer = 0;
        byte[] bnName = new byte[lenStr];
        String s = "";
        pointer += LEN_STR_SIZE;
        for (int i = 0; i < lenStr; i++) {
            bnName[i] = buffer.get(pointer);
            pointer++;
        }
        try {
            s = new String(bnName, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return s;
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
