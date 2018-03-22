import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class CSVConverter {
    //private static String csvFile = "BUSINESS_NAMES_201803.csv";
    private static String csvFile = "asic.csv";
    private static String eachline = "";

    private static void CSVtoCSV() {
        String cvsSpliter = "\\t";
        String[] registerNames;

        // line counter
        int count = 0;
        // convert the source file to file with comma splitter
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            try (PrintWriter out = new PrintWriter("output.csv")) {
                // the first line (header line) will not be changed
                if ((eachline = br.readLine()) != null) {
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
                    }
                    s = splited[splited.length - 1];
                    // the end of each line does not need comma
                    out.println(s);
                }
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
                    }
                    s = splited[splited.length - 1];
                    // the end of each line does not need comma
                    out.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("source csv length:       " + count + " lines");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // timer for calculating program execution time

        CSVtoCSV();


        long stopTime = System.currentTimeMillis();
        double elapsedTime = (stopTime - startTime) / 1000.0; // execution time of the program in seconds
        System.out.println("execution time:          " + elapsedTime + " s");
    }
}
