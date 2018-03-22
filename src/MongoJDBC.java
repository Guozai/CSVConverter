//import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoJDBC {
    public static void main(String args[]) {
        try {
            // connect to MongoDB
            MongoClient mongodb  = new MongoClient("ec2-user@s3177105.cosc2406.route53.aws.rmit.edu.au", 27017);

            // get database
            MongoDatabase db = mongodb.getDatabase("assignment1");
            System.out.println("Connect to database successfully");

            // get collection from database
            MongoCollection contacts = db.getCollection("contacts");

            // insert
            Document document = new Document("name", "mkyong");
//            document.put("age", 30);
//            document.put("createdDate", new Date());
            contacts.insertOne(document);

            FindIterable findIterable = contacts.find();
            Iterator iterator = findIterable.iterator();
            while(iterator.hasNext()) {
                Document contact = (Document) iterator.next();
                List<Document> contactNames = (List<Document>) ((Document) contact.get("metadata")).get("name");
                System.out.println(contactNames);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
