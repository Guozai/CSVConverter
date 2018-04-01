import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class HeapSearch {
    private final int HEADER_SIZE = 4;
    private final int REG_NAME_SIZE = 20;
    private final int BN_NAME_SIZE = 200;
    private final int STATUS_SIZE = 15;
    private final int DATE_SIZE = HEADER_SIZE * 3;
    private final int STATE_NUM_SIZE = 20;
    private final int STATE_SIZE = 3;
    private final int ABN_SIZE = 13;
    private final int COMMA_SIZE = 1;
    private final int RECORD_SIZE = REG_NAME_SIZE + BN_NAME_SIZE + STATUS_SIZE + 3 * DATE_SIZE + STATE_NUM_SIZE + STATE_SIZE + ABN_SIZE + COMMA_SIZE;
    private int NUM_RECORDS_PER_PAGE;
    private int numRec = 0;
    private String queryKey;
    private int pageSize;
    private String fileName;
    // count of record searched
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

                byte[] wrapper1 = new byte[REG_NAME_SIZE];
                buffer.get(wrapper1);

                for (int i = 0; i < NUM_RECORDS_PER_PAGE; i++) {
                    // business name
                    byte[] temp = new byte[BN_NAME_SIZE];
                    buffer.get(temp);
                    String s = new String(temp, "US-ASCII");
                    if (s.contains(queryKey))
                        countFound++;
                    // go through the rest of columns of the record
                    byte[] wrapper2 = new byte[RECORD_SIZE - BN_NAME_SIZE];
                    buffer.get(wrapper2);
                    // numRec increment
                    numRec++;
                }
                buffer.clear();
            }
            fc.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Number of record searched: " + numRec);
        if (countFound == 0)
            System.out.println("BN_Name does not contain " + queryKey + ".");
        else
            System.out.println(countFound + " records found containing " + queryKey + ".");
    }
}
