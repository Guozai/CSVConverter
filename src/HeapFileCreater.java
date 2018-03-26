import java.io.*;
import java.nio.ByteBuffer;

public class HeapFileCreater {
    private final int INT_SIZE = 4; // integer must be 4 bytes
    private final int REG_NAME_SIZE = 20; // REGISTER_NAME caster is limited to 20 bytes
    private final int STATUS_SIZE = 15;
    private final int DATE_SIZE = 12;
    private final int STATE_NUM_SIZE = 20;
    private final int STATE_SIZE = 3;
    private final int ABN_SIZE = 13;

    private String fileIn;
    private String eachline = "";
    private String cvsSpliter = "\\t";
    private byte[] page;
    private int pageSize;

    // pointers
    private int pos = 0; // position pointer
    private int pcol = 0; // store at which column the page reaches the end
    boolean isPageFull = false;
    private int columnNum = 0; // store the number of columns
    private int countRecord = -1; // record counter skips the header line
    private int countPage = 1; // page counter

    public HeapFileCreater(int pageSize, String fileIn) {
        this.pageSize = pageSize;
        this.fileIn = fileIn;
        this.page = new byte[pageSize];
    }

    public void launch() {
        // count the number of records
        try (BufferedReader br = new BufferedReader(new FileReader(fileIn))) {
            while ((eachline = br.readLine()) != null)
                // read one record, so counter + 1
                countRecord++;
        }catch (IOException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // read the source file
        try (BufferedReader br = new BufferedReader(new FileReader(fileIn))) {
            // skip header line but count header column number
            if ((eachline = br.readLine()) != null) {
                // split each tab-separated line into token array
                String[] splited = eachline.split(cvsSpliter);
                // count header line column number
                columnNum = splited.length;
            }

            File file = new File("heap." + Integer.toString(pageSize));
            try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {
                // if file doesn't exist, then create it
                if (!file.exists())
                    file.createNewFile();

                // save countRecord as the first entry of the binary file
                try {
                    byte[] numRec = ByteBuffer.allocate(INT_SIZE).putInt(countRecord).array();
                    ArrayCopy(numRec, page);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                }

                while((eachline = br.readLine()) != null) {
                    String[] splited = eachline.split(cvsSpliter);

                    for (int i = pcol; i < splited.length; i++) {
                        String s = splited[i]; // temporary variable to store tokens
                        if (!isPageFull)
                            saveElement(s);
                        else { // page full
                            break;
                        }
                    }

                    System.out.println("Page: " + countPage + ", Column: " + pcol);

                    if (isPageFull) {
                        fillPageWithZero(pos);
                        os.write(page);
                        os.flush();

                        isPageFull = false; // reset the flag
                        countPage++;
                        pos = 0; // reset position pointer of page
                    }
                }
                if (countPage > 1)
                    fillPageWithZero(pos);
                os.write(page);
                os.flush();
                os.close();
            }catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (IOException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Number of records loaded: " + countRecord);
        System.out.println("Number of pages used:     " + countPage);
    }

    private void saveElement(String s) {
        // this data element is empty
        if (s.length() == 0) {
            // add FF as the length of this element
            byte[] lenStr = {(byte) -1};
            ArrayCopy(lenStr, page);
            if (!isPageFull) {
                if (pcol < columnNum)
                    pcol++;
            }
        } else {
            switch (pcol) {
                case 0: // REGISTER_NAME
                    saveFixedString(s, REG_NAME_SIZE);
                    break;
                case 2: // BN_STATUS
                    saveFixedString(s, STATUS_SIZE);
                    break;
                case 3: // BN_REG_DT
                case 4: // BN_CANCEL_DT
                case 5: // BN_RENEW_DT
                    saveDate(s);
                    break;
                case 6: // BN_STATE_NUM
                    saveFixedString(s, STATE_NUM_SIZE);
                    break;
                case 7: // BN_STATE_OF_REG
                    saveFixedString(s, STATE_SIZE);
                    break;
                case 8: // BN_ABN
                    saveFixedString(s, ABN_SIZE);
                    break;
                case 1: // BN_NAME
                default:
                    saveVariableString(s);
                    break;
            }


            System.out.print(pcol + ", "); System.out.println(isPageFull);

            if (!isPageFull)
                pcol++;


            System.out.println(s + pos);


            // put comma at the end of each line
            if (pcol == columnNum) {
                byte[] lenStr = ",".getBytes();
                ArrayCopy(lenStr, page);
                if (!isPageFull)
                    pcol = 0; // reset pcol at the end of each line
            }
        }
    }

    private void saveVariableString (String s) {
        int pointer = 0;
        // get the content in bytes
        byte[] buffer = s.getBytes();
        // write the length using 4 bytes in front of register name string
        byte[] lenStr = ByteBuffer.allocate(INT_SIZE).putInt(s.length()).array();
        byte[] wrapper = new byte[lenStr.length + buffer.length];
        pointer = Copy(lenStr, wrapper, pointer);
        Copy(buffer, wrapper, pointer);
        ArrayCopy(wrapper, page);
    }

    private void saveFixedString (String s, int size) {
        int pointer = 0;
        // get the content in bytes
        byte[] buffer = s.getBytes();
        // write the length using 4 bytes in front of register name string
        byte[] lenStr = ByteBuffer.allocate(INT_SIZE).putInt(s.length()).array();
        byte[] wrapper = new byte[lenStr.length + size];
        pointer = Copy(lenStr, wrapper, pointer);
        Copy(buffer, wrapper, pointer);
        ArrayCopy(wrapper, page);
    }

    private void ArrayCopy (byte[] src, byte[] dest) {
        if ((pos + src.length) < pageSize) {
            try {
                System.arraycopy(src, 0, dest, pos, src.length);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            pos += src.length;
        } else {
            isPageFull = true;
        }
    }

    private int Copy (byte[] src, byte[] dest, int pointer) {
        try {
            System.arraycopy(src, 0, dest, pointer, src.length);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        pointer += src.length;
        return pointer;
    }

    private void saveDate (String s) {
        String[] splitted = s.split("/");
        byte[] bnDate = new byte[DATE_SIZE];
        byte[] bdate;
        try {
            // if having DD MM YYYY, do formatting
            if (splitted.length == 3) {
                for(int i = 0; i < 3; i++) {
                    if (splitted[i].length() == 0) {
                        bdate = ByteBuffer.allocate(INT_SIZE).putInt(-1).array();
                    } else {
                        int dpart = Integer.parseInt(splitted[i]);
                        bdate = ByteBuffer.allocate(INT_SIZE).putInt(dpart).array();
                    }
                    Copy(bdate, bnDate, i * bdate.length);
                }
                ArrayCopy(bnDate, page);
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
