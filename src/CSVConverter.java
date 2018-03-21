import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CSVConverter {
    public static void main(String[] args) {
        String csvFile = "BUSINESS_NAMES_201803.csv";
//        if (args[0] != null)
//            csvFile = args[0];
        String eachline = "";

        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((eachline = br.readLine()) != null) {
                count++;
            }
            System.out.println(count);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
