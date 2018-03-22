//import java.util.Iterator;
//import java.util.List;

import com.mongodb.MongoClient;
//import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoJDBC {
    public static void main(String args[]) {
        try {
            // connect to MongoDB
            MongoClient mongodb  = new MongoClient("localhost", 27017);

            // get database
            MongoDatabase db = mongodb.getDatabase("assignment1");
            System.out.println("Connect to database successfully");

            // get collection from database
            MongoCollection stateOfRegister = db.getCollection("stateOfRegister");

            // insert
            Document document = new Document("state_id", 9);
            document.put("BN_STATE_OF_REG", "AAA");
            stateOfRegister.insertOne(document);

//            FindIterable findIterable = stateOfRegister.find();
//            Iterator iterator = findIterable.iterator();
//            while(iterator.hasNext()) {
//                Document state = (Document) iterator.next();
//                List<Document> states = (List<Document>) ((Document) state.get("metadata")).get("BN_STATE_OF_REG");
//                System.out.println(states);
//            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}