import java.io.*;
import java.nio.ByteBuffer;

public class HeapFileCreater {
    private final int INT_SIZE = 4; // integer must be 4 bytes
    private final int REG_NAME_SIZE = 20; // REGISTER_NAME caster is limited to 20 bytes
    private final int STATUS_SIZE = 15;
    private final int DATE_SIZE = 12;
    private final int STATE_NUM_SIZE = 20;
    private final int STATE_SIZE = 3;
    private final int ABN_SIZE = 11;

    private String fileName;
    private String eachline = "";
    private String cvsSpliter = "\\t";
    private byte[] page;
    private int pageSize;

    // pointers
    private int pos = 0; // position pointer
    private int pcol = 0; // store at which column the page reaches the end
    boolean isPageFull = false;
    private int columnNum = 0; // store the number of columns
    private int countLine = 0; // line counter
    private int countPage = 0; // page counter

    public void launch() {

        // read the source file
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            // skip header line but count header column number
            if ((eachline = br.readLine()) != null) {
                // read one line, so counter + 1
                countLine++;
                // split each tab-separated line into token array
                String[] splited = eachline.split(cvsSpliter);
                // count header line column number
                columnNum = splited.length;
            }

            File file = new File("heap.dat");
            try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {
                // if file doesn't exist, then create it
                if (!file.exists())
                    file.createNewFile();

                while((eachline = br.readLine()) != null) {
                    countLine++;
                    String[] splited = eachline.split(cvsSpliter);
                    // temporary variable to store tokens
                    String s = "";

                    if (!isPageFull) {
                        for (int i = 0; i < splited.length; i++) {
                            s = splited[i];
                            if (s.length() == 0) {
                                // add FF as the length of this element
                                byte[] lenStr = {(byte)-1};
                                ArrayCopy(lenStr, 0, page);
                                if (!isPageFull) {
                                    if (pcol < columnNum)
                                        pcol++;
                                }
                            } else {
                                switch (i) {
                                    case 0: // REGISTER_NAME
                                        saveFixedString(s, REG_NAME_SIZE);
                                        if (!isPageFull)
                                            pcol++;
                                        break;
                                    case 2: // BN_STATUS
                                        saveFixedString(s, STATUS_SIZE);
                                        if (!isPageFull)
                                            pcol++;
                                        break;
                                    case 3: // BN_REG_DT
                                    case 4: // BN_CANCEL_DT
                                    case 5: // BN_RENEW_DT
                                        saveDate(s, DATE_SIZE);
                                        if (!isPageFull)
                                            pcol++;
                                        break;
                                    case 6: // BN_STATE_NUM
                                        saveFixedString(s, STATE_NUM_SIZE);
                                        if (!isPageFull)
                                            pcol++;
                                        break;
                                    case 7: // BN_STATE_OF_REG
                                        saveFixedString(s, STATE_SIZE);
                                        if (!isPageFull)
                                            pcol++;
                                        break;
                                    case 8: // BN_ABN
                                        saveFixedString(s, ABN_SIZE);
                                        if (!isPageFull)
                                            pcol++;
                                        break;
                                    case 1: // BN_NAME
                                    default:
                                        saveVariableString(s);
                                        if (!isPageFull)
                                            pcol++;
                                        break;
                                }
                            }
                            // put comma at the end of each line
                            if (pcol == splited.length) {
                                byte[] lenStr = ",".getBytes();
                                ArrayCopy(lenStr, 0, page);
                                if (!isPageFull)
                                    pcol = 0;
                            }
                        }
                    } else { // page full
                        writeZero(pos);
                        os.write(page);
                        os.flush();

                        isPageFull = false; // reset the flag
                        pos = 0; // reset position pointer of page
                        for (int j = pcol; j < splited.length; j++) {
                            s = splited[j];
                            if (s == "") {

                            } else {

                            }
                            if (pcol == splited.length) {
                                // put comma at the end of each line
                                byte[] lenStr = ",".getBytes();
                                ArrayCopy(lenStr, 0, page);
                                if (!isPageFull)
                                    pcol = 0;
                            }
                        }
                        pcol = 0;
                    }

                }

                // remove this line later
                os.write(page);

                System.out.println(os.size() + " bytes were written.");

                os.flush();
                os.close();

            }catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (IOException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void saveVariableString (String s) {
        // get the content in bytes
        byte[] buffer = s.getBytes();
        // write the length using 1 byte in front of register name string
        byte[] lenStr = {(byte)s.length()};
        ArrayCopy(lenStr, 0, page);
        ArrayCopy(buffer, 0, page);
    }

    private void saveFixedString (String s, int size) {
        // get the content in bytes
        byte[] buffer = s.getBytes();
        // write the length using 1 byte in front of register name string
        byte[] lenStr = {(byte)s.length()};
        ArrayCopy(lenStr, 0, page);
        // ensure fixed length of 20 bytes
        byte[] caster = new byte[size];
        System.arraycopy(buffer, 0, caster, 0, buffer.length);
        ArrayCopy(caster, 0, page);
    }

    private void ArrayCopy (byte[] src, int srcPos, byte[] dest) {
        if ((pos + src.length) < pageSize) {
            System.arraycopy(src, srcPos, dest, pos, src.length);
            pos += src.length;
        } else {
            isPageFull = true;
        }
    }

    private void saveDate (String s, int size) {
        try {
            String[] splitted = s.split("/");
            // if having DD MM YYYY, do formatting
            if (splitted.length == 3) {
                byte[] bnDate = new byte[size];
                for(int i = 0; i < 3; i++) {
                    int dpart = Integer.parseInt(splitted[i]);
                    byte[] bdate = ByteBuffer.allocate(INT_SIZE).putInt(dpart).array();

                    try {
                        System.arraycopy(bdate, 0, bnDate, i * bdate.length, bdate.length);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    }
                }
                if ((pos + bnDate.length) < pageSize) {
                    System.arraycopy(bnDate, 0, page, pos, bnDate.length);
                    pos += bnDate.length;
                } else {
                    isPageFull = true;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void writeZero (int pos) {
        byte[] buffer = ByteBuffer.allocate(pageSize - pos).putInt(0).array();
        try {
            System.arraycopy(buffer, 0, page, pos, buffer.length);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void setPage(int pageSize) {
        this.page = new byte[pageSize];
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        setPage(pageSize);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
