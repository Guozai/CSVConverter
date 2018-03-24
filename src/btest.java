import java.io.*;
import java.nio.ByteBuffer;

public class btest {
    final int pageSize = 32;
    String eachline = "";
    String cvsSpliter = "\\t";
    byte[] page = new byte[pageSize];
    byte[] regName = new byte[20]; //
    int columnNum = 0; // store the number of columns
    int pos = 0;

    public void launch() {
        // read the source file
        try (BufferedReader br = new BufferedReader(new FileReader("asic.csv"))) {
            // skip header line but count header column number
            if ((eachline = br.readLine()) != null) {
                // split each tab-separated line into token array
                String[] splited = eachline.split(cvsSpliter);
                // count header line column number
                columnNum = splited.length;
            }
            File file = new File("test.dat");
            try (DataOutputStream os = new DataOutputStream(new FileOutputStream(file))) {
                // if file doesn't exist, then create it
                if (!file.exists())
                    file.createNewFile();

                fillPage(pos);
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
    }

    private void ArrayCopy (byte[] src, int srcPos, byte[] dest) {
        System.arraycopy(src, srcPos, dest, pos, src.length);
        pos += src.length;
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

    private void fillPage (int pos) {
        byte[] buffer = ByteBuffer.allocate(4).putInt(-1).array();
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
