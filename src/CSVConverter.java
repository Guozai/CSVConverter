import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class CSVConverter {
    private static String csvFile = "BUSINESS_NAMES_201803.csv";
    //private static String csvFile = "asic.csv";
    private static String registerName = "BUSINESS NAMES";
    private static String[] status = new String[] {"Registered", "Deregistered"};
    private static String[] states = new String[] {"ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC", "WA"};
    private static String eachline = "";
    private static String cvsSpliter = "\\t";

    private static void CSVtoCSV() {
        // line counter
        int count = 0;
        // convert the source file to file with comma splitter
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            try (PrintWriter out = new PrintWriter("bn.csv")) {
                while ((eachline = br.readLine()) != null) {
                    // count number of lines
                    count++;
                    // split each tab-separated line into token array
                    String[] splited = eachline.split(cvsSpliter);
                    // temporary variable to store tokens
                    String s = "";
                    for (int i = 0; i < splited.length; i++) {
                        s = splited[i];
                        // the first line (header line) will not be changed
                        if (count == 1) {
                            // write to fire changing tab to comma
                            out.print(s);
                            // put comma behind each token except the last one
                            if (i < splited.length - 1)
                                out.print(",");
                            else
                                // end of line
                                out.println();
                        } else {
                            int id;
                            switch (s) {
                                // column REGISTER_NAME type 1
                                case "BUSINESS NAMES":
                                    id = 1;
                                    out.print(id);
                                    break;
                                // column BN_STATUS
                                case "Registered":
                                    id = 1;
                                    out.print(id);
                                    break;
                                case "Deregistered":
                                    id = 2;
                                    out.print(id);
                                    break;
                                // column BN_STATE_OF_REG
                                case "NSW":
                                    id = 2;
                                    out.print(id);
                                    break;
                                case "VIC":
                                    id = 7;
                                    out.print(id);
                                    break;
                                case "QLD":
                                    id = 4;
                                    out.print(id);
                                    break;
                                case "WA":
                                    id = 8;
                                    out.print(id);
                                    break;
                                case "SA":
                                    id = 5;
                                    out.print(id);
                                    break;
                                case "ACT":
                                    id = 1;
                                    out.print(id);
                                    break;
                                case "TAS":
                                    id = 6;
                                    out.print(id);
                                    break;
                                case "NT":
                                    id = 3;
                                    out.print(id);
                                    break;
                                default:
                                    // write to fire changing tab to comma
                                    out.print(s);
                                    break;
                            }
                            // put comma behind each token except the last one
                            if (i < splited.length - 1)
                                out.print(",");
                            else
                                // end of line
                                out.println();
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            System.out.println("source csv length:       " + count + " lines");
        } catch (IOException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private static void createCSV(String[] dataset, String fileName, String header) {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println(header);
            for (int i = 0; i < dataset.length; i++) {
                out.print((i+1) + ",");
                out.println(dataset[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createCSVSingleString(String data, String fileName) {
        try (PrintWriter out = new PrintWriter(fileName)) {
            out.println("reg_id,REGISTER_NAME");
            out.println(1 + "," + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // timer for calculating program execution time

        CSVtoCSV();
        createCSVSingleString(registerName, "regName.csv");
        createCSV(status, "status.csv", "status_id,BN_STATUS");
        createCSV(states, "states.csv", "state_id,BN_STATE_OF_REG");


        long stopTime = System.currentTimeMillis();
        double elapsedTime = (stopTime - startTime) / 1000.0; // execution time of the program in seconds
        System.out.println("execution time:          " + elapsedTime + " s");
    }
}
