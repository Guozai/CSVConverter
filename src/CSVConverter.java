import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class CSVConverter {
    public static void main(String[] args) {
        String csvFile = "BUSINESS_NAMES_201803.csv";
        if (args[0] != null)
            csvFile = args[0];
        String eachline = "";
        String cvsSpliter = "\\t";

        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            try (PrintWriter out = new PrintWriter("output.csv")) {
                while ((eachline = br.readLine()) != null) {
                    // split each tab-separated line into token array
                    String[] splited = eachline.split(cvsSpliter);
                    // temporary variable to store tokens
                    String s = "";
                    for (int i = 0; i < splited.length - 1; i++) {
                        s = splited[i];
                        // write to fire changing tab to comma
                        out.print(s + ",");
                        System.out.print(s + ",");
                    }
                    s = splited[splited.length - 1];
                    // the end of each line does not need comma
                    out.println(s);
                    System.out.println(s);
                    // count number of lines
                    count++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(count);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
