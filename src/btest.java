import java.io.*;

public class btest {
    String eachline = "";
    String cvsSpliter = "\\t";
    byte[] page = new byte[1024];
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

                while((eachline = br.readLine()) != null) {
                    String[] splited = eachline.split(cvsSpliter);
                    String s = "";
                    for (int i = 0; i < splited.length; i++) {
                        s = splited[i];
                        if (s.length() == 0) {
                            System.out.println("found empty.");
                            // add FF as the length of this element
                            byte[] lenStr = {(byte) -1};
                            ArrayCopy(lenStr, 0, page);
                        }
                    }
                }
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
}
