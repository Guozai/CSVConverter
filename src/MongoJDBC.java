import com.mongodb.*;
//import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MongoJDBC {
    //private static String csvFile = "BUSINESS_NAMES_201803.csv";
    private static String csvFile = "asic.csv";
    private static String[] colHeader;

    private static String registerName = "BUSINESS NAMES";
    private static String[] status = new String[] {"Registered", "Deregistered"};
    private static String[] states = new String[] {"ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC", "WA"};
    private static String eachline = "";
    private static String cvsSpliter = "\\t";

    private static void importMongo(MongoCollection businessName) {
        Document bnName = new Document();
        List<Document> bnNames = new ArrayList<>();

        // line counter
        int count = 0;
        // convert the source file to file with comma splitter
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
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
                        // get the header of each oolumn
                        colHeader[i] = s;
                    } else {
                        if (s.length() == 0)
                            bnName.put(colHeader[i], "");
                        else {
                            // remove repeated information that is consistent
                            switch (i) {
                                // column REGISTER_NAME type 1
                                case 0:
                                    if (s.equals("BUSINESS NAMES"))
                                        bnName.put(colHeader[i], 1);
                                    else
                                        bnName.put(colHeader[i], s);
                                    break;
                                // column BN_NAME
                                case 1:
                                    bnName.put(colHeader[i], s);
                                    break;
                                // BN_STATUS
                                case 2:
                                    if (s.equals("Registered"))
                                        bnName.put(colHeader[i], 1);
                                    else if (s.equals("Deregistered"))
                                        bnName.put(colHeader[i], 2);
                                    else
                                        bnName.put(colHeader[i], s);
                                    break;
                                case 3: // BN_REG_DT
                                case 4: // BN_CANCEL_DT
                                case 5: // BN_RENEW_DT
                                    bnName.put(colHeader[i], s);
                                    break;
                                case 6: // BN_STATE_NUM
                                    bnName.put(colHeader[i], s);
                                    break;
                                // column BN_STATE_OF_REG
                                case 7:
                                    if (s.equals("NSW"))
                                        bnName.put(colHeader[i], 2);
                                    else if (s.equals("VIC"))
                                        bnName.put(colHeader[i], 7);
                                    else if (s.equals("QLD"))
                                        bnName.put(colHeader[i], 4);
                                    else if (s.equals("SA"))
                                        bnName.put(colHeader[i], 5);
                                    else if (s.equals("WA"))
                                        bnName.put(colHeader[i], 8);
                                    else if (s.equals("ACT"))
                                        bnName.put(colHeader[i], 1);
                                    else if (s.equals("TAS"))
                                        bnName.put(colHeader[i], 6);
                                    else if (s.equals("NT"))
                                        bnName.put(colHeader[i], 3);
                                    else
                                        bnName.put(colHeader[i], s);
                                    break;
                                case 8:
                                    bnName.put(colHeader[i], s);
                                    break;
                                default: // BN_ABN
                                    break;
                            }
                        }
                        if (i < 8) {
                            for (int j = i + 1; j < 9; j++) {
                                bnName.put(colHeader[j], "");
                            }
                        }
                    }
                    bnNames.add(bnName);
                }
                // insert
                businessName.insertMany(bnNames);
            }
        } catch (IOException e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("source csv length:       " + count + " lines");

//            FindIterable findIterable = stateOfRegister.find();
//            Iterator iterator = findIterable.iterator();
//            while(iterator.hasNext()) {
//                Document state = (Document) iterator.next();
//                List<Document> states = (List<Document>) ((Document) state.get("metadata")).get("BN_STATE_OF_REG");
//                System.out.println(states);
//            }

    }

    private static void createCollectionSingleElement(String element, MongoCollection collection, String headerId, String headerContent) {
        Document document = new Document(headerId, 1);
        document.put(headerContent, element);
        collection.insertOne(document);
    }

    private static void createCollection(String[] elements, MongoCollection collection, String headerId, String headerContent) {
        for (int i = 0; i < elements.length; i++) {
            Document document = new Document(headerId, i);
            document.put(headerContent, elements[i]);
            collection.insertOne(document);
        }
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // timer for calculating program execution time

        try {
            // connect to MongoDB
            MongoClient mongodb = new MongoClient("localhost", 27017);

            // get database
            MongoDatabase db = mongodb.getDatabase("assignment1");
            System.out.println("Connect to database successfully");

            // get collection from database
//            MongoCollection businessName = db.getCollection("businessName");
//            importMongo(businessName);
            MongoCollection regName = db.getCollection("registerName");
            createCollectionSingleElement(registerName, regName, "reg_id", "REGISTRATION_NAME");
            MongoCollection registerStatus = db.getCollection("registerStatus");
            createCollection(status, registerStatus, "reg_id", "BN_STATUS");
            MongoCollection stateOfRegister = db.getCollection("stateOfRegister");
            createCollection(states, stateOfRegister, "state_id", "BN_STATE_OF_REG");

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        long stopTime = System.currentTimeMillis();
        double elapsedTime = (stopTime - startTime) / 1000.0; // execution time of the program in seconds
        System.out.println("execution time:          " + elapsedTime + " s");
    }
}