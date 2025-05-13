package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.bson.Document;
import org.example.kaisse.SceneManager;
import org.example.kaisse.Main;
import org.example.kaisse.model.User;

import java.io.IOException;

public class RegisterController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void handleSubmit(ActionEvent event) throws IOException {
        User userTest = new User("John", "Cashier", 35f);

        MongoDatabase database = Main.database;
        MongoCollection<Document> collection = database.getCollection("User");

        Document userDoc = new Document("name", userTest.getName())
                .append("role", userTest.getJob())
                .append("hours", userTest.getWorkTime());

        try {
            collection.insertOne(userDoc);
            System.out.println("Inserted: " + userTest);
        } catch (Exception e) {
            System.out.println("Fail Insert: " + e);
        }

        //SceneManager.changeScene("main-view", event);

    }
}