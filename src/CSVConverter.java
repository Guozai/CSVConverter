import java.io.BufferedReader;
        import java.io.FileReader;
        import java.io.IOException;

public class CSVConverter {
    public static void main(String[] args) {
        String csvFile = "BUSINESS_NAMES_201803.csv";
        if (args[0] != null)
            csvFile = args[0];
        String eachline = "";
        String cvsSpliter = "\\s+";

        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((eachline = br.readLine()) != null) {
                String[] splited = eachline.split(cvsSpliter);
                for (String s : splited)
                    System.out.print(s + ", ");
                count++;
            }
            System.out.println(count);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
