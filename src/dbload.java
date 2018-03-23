import java.io.*;
import java.util.ArrayList;

public class dbload {
    private static int pos = 0; // position pointer

    public static void main(String[] args) {
        File file = new File("heap.dat");
        String eachline = "";
        String cvsSpliter = "\\t";
        byte[] page = new byte[128];
        byte[] bInt = new byte[4]; // integer must be 4 bytes
        byte[] regName = new byte[20]; // REGISTER_NAME is limited to 20 bytes
        byte[] bnName = new byte[80]; // BN_NAME
        byte[] bnStatus = new byte[15]; // BN_STATUS
        byte[] bnDate = new byte[10]; // BN_REG_DT BN_CANCEL_DT BN_RENEW_DT are all limited to 10 bytes
        byte[] bnStateNum = new byte[20]; // BN_STATE_NUM
        byte[] bnState = new byte[3]; // BN_STATE_OF_REG
        byte[] bnABN = new byte[11]; // BN_ABN
        int columnNum = 0; // store the number of columns
        int countLine = 0; // line counter
        int countPage = 0; // page counter

        // read the source file
        try (BufferedReader br = new BufferedReader(new FileReader("asic.csv"))) {
            // skip header line but count header column number
            if ((eachline = br.readLine()) != null) {
                // read one line, so counter + 1
                countLine++;
                // split each tab-separated line into token array
                String[] splited = eachline.split(cvsSpliter);
                // count header line column number
                columnNum = splited.length;
            }
            try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {
                // if file doesn't exist, then create it
                if (!file.exists())
                    file.createNewFile();

                while((eachline = br.readLine()) != null) {
                    countLine++;
                    String[] splited = eachline.split(cvsSpliter);
                    // temporary variable to store tokens
                    String s = "";
                    for (int i = 0; i < splited.length; i++) {
                        s = splited[i];
                        switch(i) {
                            case 0: // REGISTER_NAME
                                saveElement (s, regName, page, pos);
                                break;
                            case 1: // BN_NAME
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 5:
                                break;
                            case 6:
                                break;
                            case 7:
                                break;
                            case 8:
                                break;
                            default:
                                break;
                        }
//                        // put comma behind each token except the last one
//                        if (i < splited.length - 1) {
//                            out.print(",");
//                        }
//                        else {
//                            // must ensure each row have 9 elements (8 commas)
//                            if (splited.length < columnNum) {
//                                for (int j = splited.length; j < columnNum; j++)
//                                    out.print(",");
//                            }
//                            // end of line
//                            out.println();
//                        }
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

    private static void saveElement (String s, byte[] src, byte[] page, int pos) {
        // get the content in bytes
        byte[] buffer = s.getBytes();
        // write the length using 1 byte in front of register name string
        byte[] lenStr = {(byte)s.length()};
        System.out.println(lenStr.length);
        if ((pos + lenStr.length) < 4096) {
            System.arraycopy(lenStr, 0, page, pos, lenStr.length);
            pos += lenStr.length;
        }
        // ensure fixed length of 20 bytes
        System.arraycopy(buffer, 0, src, 0, buffer.length);
        if ((pos + src.length) < 4096) {
            System.arraycopy(src, 0, page, pos, src.length);
            pos += src.length;
        }
    }

    private static int ArrayCopy (byte[] src, int srcPos, byte[] dest, int pos) {
        if ((pos + src.length) < 4096) {
            System.arraycopy(src, srcPos, dest, pos, src.length);
            pos += src.length;
        }
        return pos;
    }
}
