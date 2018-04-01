import java.io.*;
import java.nio.ByteBuffer;

public class HeapCreate {
    private final int INT_SIZE = 4; // integer must be 4 bytes
    private final int REG_NAME_SIZE = 20; // REGISTER_NAME caster is limited to 20 bytes
    private final int BN_NAME_SIZE = 200;
    private final int STATUS_SIZE = 15;
    private final int DATE_SIZE = 12;
    private final int STATE_NUM_SIZE = 20;
    private final int STATE_SIZE = 3;
    private final int ABN_SIZE = 13;
    private final int COMMA_SIZE = 1;
    private final int RECORD_SIZE = REG_NAME_SIZE + BN_NAME_SIZE + STATUS_SIZE + 3 * DATE_SIZE + STATE_NUM_SIZE + STATE_SIZE + ABN_SIZE + COMMA_SIZE;
    private final int COLUMN_NUM = 9;

    private byte[] page;
    private int pageSize;
    private String fileIn;
    private String eachline = "";
    private String cvsSpliter = "\\t";

    // pointers
    private int pos = 0; // page position pointer

    // counters
    private int countRecord = 0;
    private int countPage = 0;

    public HeapCreate (int pageSize, String fileIn) {
        this.pageSize = pageSize;
        this.fileIn = fileIn;
        this.page = new byte[pageSize];
    }

    public void create() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileIn))) {
            // skip header line
            eachline = br.readLine();

            File file = new File("heap." + Integer.toString(pageSize));
            try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {
                // if file doesn't exist, then create it
                if (!file.exists())
                    file.createNewFile();

                while((eachline = br.readLine()) != null) {

                    String[] splited = eachline.split(cvsSpliter);

                    if (pos + RECORD_SIZE < pageSize) {
                        for (int i = 0; i < splited.length; i++) {
                            saveElement(splited[i], i);
                        }
                        // add comma at the end of each record
                        byte[] comma = ",".getBytes();
                        ArrayCopy(comma, page, pos);
                        pos += COMMA_SIZE;
                    } else {
                        fillPageWithZero(pos);
                        os.write(page);
                        os.flush();

                        countPage++;
                        pos = 0;  // reset position pointer of page
                    }

                    countRecord++;
                }

            } catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (IOException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Number of records loaded: " + countRecord);
        System.out.println("Number of pages used:     " + countPage);
    }

    private void saveElement(String s, int i) {
        switch (i) {
            case 0: // REGISTER_NAME
                // this data element is empty
                if (s.length() == 0)
                    saveFixedNull(pos);
                else
                    saveFixedString(s, REG_NAME_SIZE, pos);
                pos += REG_NAME_SIZE;
                break;
            case 1: // BN_NAME
                if (s.length() == 0)
                    saveFixedNull(pos);
                else
                    saveFixedString(s, BN_NAME_SIZE, pos);
                pos += BN_NAME_SIZE;
                break;
            case 2: // BN_STATUS
                if (s.length() == 0)
                    saveFixedNull(pos);
                else
                    saveFixedString(s, STATUS_SIZE, pos);
                pos += STATUS_SIZE;
                break;
            case 3: // BN_REG_DT
            case 4: // BN_CANCEL_DT
            case 5: // BN_RENEW_DT
                if (s.length() == 0)
                    saveFixedNull(pos);
                else
                    saveDate(s, pos);
                pos += DATE_SIZE;
                break;
            case 6: // BN_STATE_NUM
                if (s.length() == 0)
                    saveFixedNull(pos);
                else
                    saveFixedString(s, STATE_NUM_SIZE, pos);
                pos += STATE_NUM_SIZE;
                break;
            case 7: // BN_STATE_OF_REG
                if (s.length() == 0)
                    saveFixedNull(pos);
                else
                    saveFixedString(s, STATE_SIZE, pos);
                pos += STATE_SIZE;
                break;
            case 8: // BN_ABN
                if (s.length() == 0)
                    saveFixedNull(pos);
                else
                    saveFixedString(s, ABN_SIZE, pos);
                pos += ABN_SIZE;
                break;
            default:
                break;
        }
    }

    private void saveFixedNull (int pos) {
        // add FF as the length of this element
        byte[] lenStr = ByteBuffer.allocate(INT_SIZE).putInt(-1).array();
        try {
            System.arraycopy(lenStr, 0, page, pos, lenStr.length);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void saveFixedString (String s, int size, int pos) {
        // get the content in bytes
        byte[] buffer = s.getBytes();
        ArrayCopy(buffer, page, pos);
    }

    private void ArrayCopy (byte[] src, byte[] dest, int pointer) {
        try {
            System.arraycopy(src, 0, dest, pointer, src.length);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void saveDate (String s, int pos) {
        String[] splitted = s.split("/");
        byte[] dateWrapper = new byte[DATE_SIZE];
        try {
            // if having DD MM YYYY, do formatting
            if (splitted.length == 3) {
                for(int i = 0; i < 3; i++) {
                    int dpart = Integer.parseInt(splitted[i]);
                    byte[] bdate = ByteBuffer.allocate(INT_SIZE).putInt(dpart).array();
                    ArrayCopy(bdate, dateWrapper, i * bdate.length);
                }
                ArrayCopy(dateWrapper, page, pos);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void fillPageWithZero (int pos) {
        byte[] buffer = {(byte)0};
        while (pos + buffer.length <= pageSize) {
            try {
                System.arraycopy(buffer, 0, page, pos, buffer.length);
                pos += buffer.length;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
}
