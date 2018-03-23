import java.io.*;

public class btest {
    public static void main(String[] args) {
        File file = new File("test.dat");
        String eachline = "";
        String cvsSpliter = "\\t";
        byte[] page = new byte[16];
        byte[] regName = new byte[20]; //
        int columnNum = 0; // store the number of columns

        int field = 2;

        // read the source file
        try (BufferedReader br = new BufferedReader(new FileReader("test.csv"))) {
            // skip header line but count header column number
            if ((eachline = br.readLine()) != null) {
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
                    // get the content in bytes
                    byte[] buffer = eachline.getBytes();

                    //os.write(contentInBytes);

                    //String binaryStr = Integer.toBinaryString(field);
                    //int numBits = 32;
                    //binaryStr = binaryStr.substring(binaryStr.length() - numBits >= 0 ? binaryStr.length() - numBits : 0);

                    System.arraycopy(buffer, 0, regName, 3, buffer.length);

                    byte[] lenStr = {(byte)14};
                    System.arraycopy(lenStr, 0, page, 1, lenStr.length);
                    os.write(page);
                    System.out.println(os.size() + " bytes were written.");

                    //os.writeBytes(binaryStr);
                    //os.writeBytes(binaryIntInStr);

                    //System.out.println("bitCout: " + numBits);
                }
                os.flush();
                os.close();

            }catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (IOException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
