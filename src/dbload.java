import java.io.*;

public class dbload {
    public static void main(String[] args) {
        File file = new File("heap.dat");
        String eachline = "";
        String cvsSpliter = "\\t";
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
                    byte[] contentInBytes = eachline.getBytes();

                    //os.write(contentInBytes);

                    //String binaryStr = Integer.toBinaryString(field);
                    //int numBits = 32;
                    //binaryStr = binaryStr.substring(binaryStr.length() - numBits >= 0 ? binaryStr.length() - numBits : 0);

                    os.writeInt(field);
                    System.out.println(os.size() + " bytes were written.");

                    //os.writeBytes(binaryStr);
                    //os.writeBytes(binaryIntInStr);

                    //System.out.println("bitCout: " + numBits);
                }
            //os.flush();
            os.close();

            }catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (IOException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
