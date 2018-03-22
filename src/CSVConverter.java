import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class CSVConverter {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // timer for calculating program execution time
        String csvFile = "BUSINESS_NAMES_201803.csv";
        if (args[0] != null)
            csvFile = args[0];
        String eachline = "";
        String cvsSpliter = "\\t";
        //String[] registerNames;

        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            try (PrintWriter out = new PrintWriter("output.csv")) {
                while ((eachline = br.readLine()) != null) {
                    // count number of lines
                    count++;
                    // split each tab-separated line into token array
                    String[] splited = eachline.split(cvsSpliter);
                    // temporary variable to store tokens
                    String s = "";
                    for (int i = 0; i < splited.length - 1; i++) {
                        s = splited[i];
                        // write to fire changing tab to comma
                        out.print(s + ",");
                        //System.out.print(s + ",");
                    }
                    s = splited[splited.length - 1];
                    // the end of each line does not need comma
                    out.println(s);
                    //System.out.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("source csv length:       " + count + " lines");
        } catch (IOException e){
            e.printStackTrace();
        }

//        // convert the source file to file with comma splitter
//        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
//            try (PrintWriter out = new PrintWriter("output.csv")) {
//                while ((eachline = br.readLine()) != null) {
//                    // split each tab-separated line into token array
//                    String[] splited = eachline.split(cvsSpliter);
//                    // temporary variable to store tokens
//                    String s = "";
//                    for (int i = 0; i < splited.length - 1; i++) {
//                        s = splited[i];
//                        // write to fire changing tab to comma
//                        out.print(s + ",");
//                        //System.out.print(s + ",");
//                    }
//                    s = splited[splited.length - 1];
//                    // the end of each line does not need comma
//                    out.println(s);
//                    //System.out.println(s);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e){
//            e.printStackTrace();
//        }

//        int count = 0;
//        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
//            try (PrintWriter out = new PrintWriter("output.csv")) {
//                while ((eachline = br.readLine()) != null) {
//                    // count number of lines
//                    count++;
//                    // split each tab-separated line into token array
//                    String[] splited = eachline.split(cvsSpliter);
//                    // temporary variable to store tokens
//                    String s = "";
//                    for (int i = 0; i < splited.length - 1; i++) {
//                        s = splited[i];
//                        // the first line (header line) will not be changed
//                        if (count == 1) {
//                            // write to fire changing tab to comma
//                            out.print(s + ",");
//                            //System.out.print(s + ",");
//                        }
//                    }
//                    s = splited[splited.length - 1];
//                    // the end of each line does not need comma
//                    out.println(s);
//                    //System.out.println(s);
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println(count);
//        } catch (IOException e){
//            e.printStackTrace();
//        }

        long stopTime = System.currentTimeMillis();
        double elapsedTime = (stopTime - startTime) / 1000.0;
        System.out.println("execution time:          " + elapsedTime + " s");
    }
}
