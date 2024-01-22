package main.java.org.example;

import java.io.FileInputStream;
import java.util.Properties;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDB {

    private final String FILE_PROPS = "app.config";
    private static MongoClient db = null;

    private MongoDB() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(FILE_PROPS));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String uri = props.getProperty("protocol")
                + "://"
                + props.getProperty("user")
                + ":"
                + props.getProperty("pass")
                + "@"
                + props.getProperty("host");
        db = MongoClients.create(uri);
    }

    public static MongoClient getClient() {
        if (db == null) {
            new MongoDB();
        }
        return db;
    }
}