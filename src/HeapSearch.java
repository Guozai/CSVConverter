import java.io.*;

public class HeapSearch {
    private String queryKey;
    private int pageSize;

    private int pos = 0; // position pointer
    private byte[] buf; // byte[] buffer

    public HeapSearch (String queryKey, int pageSize) {
        this.queryKey = queryKey;
        this.pageSize = pageSize;
    }

    public void launch() {
        // read the source file
        try (FileInputStream fileIn = new FileInputStream(new File("heap." + Integer.toString(pageSize)))) {
            // create data input stream
            DataInputStream in = new DataInputStream(fileIn);
            // count the available bytes form the input stream

            System.out.println(in.available());
            System.out.println(in.available()/pageSize);

            int lenStr = (int) in.readByte();
//            System.out.println((int) in.readByte());
            pos += 1;
//            in.read(buf, pos, lenStr);
//            pos += lenStr;

            fileIn.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
