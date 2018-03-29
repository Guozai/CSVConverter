import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mongotest {
    //private static String csvFile = "BUSINESS_NAMES_201803.csv";
    private static String csvFile = "asic.csv";
    private static String[] colHeader;

    private static String registerName = "BUSINESS NAMES";
    private static String[] status = new String[] {"Registered", "Deregistered"};
    private static String[] states = new String[] {"ACT", "NSW", "NT", "QLD", "SA", "TAS", "VIC", "WA"};
    private static String eachline = "";
    private static String cvsSpliter = "\\t";

    private static void importMongo(MongoCollection businessName) {
        List<Document> bnNames = new ArrayList();
        Document bnName = new Document();
        bnName.append("name", "john");
        bnName.append("age", 35);
        bnName.append("kids", "aaa");
        bnName.append("info", new BasicDBObject("email", "john@mail.com")
                        .append("phone", "876-134-667"));

        bnNames.add(bnName);

        Document bnName1 = new Document();
        bnName1.append("name", "Mike");
        bnName1.append("age", 21);
        bnName1.append("kids", "");
        bnName1.append("info", new BasicDBObject("email", "john@mail.com")
                .append("phone", "876-134-667"));

        bnNames.add(bnName1);

        businessName.insertMany(bnNames);

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
            MongoCollection businessName = db.getCollection("businessName");
            importMongo(businessName);
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
