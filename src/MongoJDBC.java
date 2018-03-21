import java.net.UnknownHostException;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class MongoJDBC {
    public static void main(String args[]) {
        try {
            // connect to MongoDB
            MongoClient mongodb  = new MongoClient("localhost", 27017);

            // get database
            DB db = mongodb.getDB("assignment1");
            System.out.println("Connect to database successfully");

            // get collection from database
            DBCollection table = db.getCollection("user");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
