package db

import com.mongodb.Block
import com.mongodb.MongoClient
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoCursor
import com.mongodb.client.MongoDatabase
import groovy.json.JsonSlurper
import org.bson.Document

import static com.mongodb.client.model.Filters.eq

// https://mvnrepository.com/artifact/org.mongodb/mongo-java-driver
//@Grapes(
@Grab(group = 'org.mongodb', module = 'mongo-java-driver', version = '3.12.1')
//)

MongoClient mongoClient = new MongoClient("localhost", 27017);
MongoDatabase database = mongoClient.getDatabase("testMongoDb");

MongoCollection<Document> collection = database.getCollection("test");

// collection.deleteMany(new Document());
collection.drop()

String localFileName = "../text/LocalDXYArea.json"
String jsonString = new File(localFileName).text

def rawStats = new JsonSlurper().parseText(jsonString)

rawStats.results.each { stat ->
        collection.insertOne(new Document(stat))
}
/*
Document doc = Document.parse(jsonString)
collection.insertOne(doc)
*/

/*MongoCursor<Document> cursor = collection.find().iterator();
try {
    while (cursor.hasNext()) {
        println(cursor.next().toJson());
    }
} finally {
    cursor.close();
}*/

Block<Document> printBlock = new Block<Document>() {
    @Override
    public void apply(final Document document) {
        println(document.toJson());
    }
};

// collection.find(eq("results.locationId", 610000)).forEach(printBlock);
// collection.find({ results: { locationId: 610000 }}) //.forEach(printBlock);
FindIterable<Document> iterable = collection.find(eq("locationId", 610000))

println iterable.toList()




