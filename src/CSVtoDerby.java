import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVtoDerby {
    private static String csvFile = "asic.csv";
    //private static String registerName = "BUSINESS NAMES";
    private static String[] status = new String[] {"Registered", "Deregistered"};
    private static String[] states = new String[] {"ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC", "WA"};
    private static String eachline = "";
    private static String cvsSpliter = "\\t";

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // timer for calculating program execution time

        CSVtoDEL();

        long stopTime = System.currentTimeMillis();
        double elapsedTime = (stopTime - startTime) / 1000.0; // execution time of the program in seconds
        System.out.println("execution time:          " + elapsedTime + " s");
    }

    private static void CSVtoDEL() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            try (PrintWriter out = new PrintWriter("bn.del")) {
                // skip header line
                eachline = br.readLine();
                while ((eachline = br.readLine()) != null) {
                    // split each tab-separated line into token array
                    String[] splited = eachline.split(cvsSpliter);
                    // temporary variable to store tokens
                    String s = "";
                    for (int i = 0; i < splited.length; i++) {
                        s = splited[i];
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
            } catch (IOException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
