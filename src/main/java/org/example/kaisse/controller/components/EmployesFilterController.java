package org.example.kaisse.controller.components;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.bson.Document;
import org.example.kaisse.Main;
import org.example.kaisse.model.Table;
import org.example.kaisse.model.User;

import java.util.ArrayList;
import java.util.List;

public class EmployesFilterController {
    @FXML
    private ListView<String> miniList;

    @FXML
    private ListView<String> midList;

    @FXML
    private ListView<String> highList;

    @FXML
    protected void getUserAgeFromDatabase() {
        try {
            MongoDatabase database = Main.database;
            MongoCollection<Document> collection = database.getCollection("User");

            List<Document> documents = collection.find().into(new ArrayList<>());

            List<User> users = documents.stream()
                    .map(User::createFromDocument)
                    .toList();

            ObservableList<String> miniListItems = FXCollections.observableArrayList();
            ObservableList<String> midListItems = FXCollections.observableArrayList();
            ObservableList<String> highListItems = FXCollections.observableArrayList();

            for (User user : users) {
                String display =  user.getName() + " " + user.getAge() + " ans" ;
                if (user.getAge() < 30) {
                    miniListItems.add(display);
                } else if (user.getAge() <= 45) {
                    midListItems.add(display);
                } else {
                    highListItems.add(display);
                }
            }

            miniList.setItems(miniListItems);
            midList.setItems(midListItems);
            highList.setItems(highListItems);


        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    public void initialize() {
        getUserAgeFromDatabase();
    }
}
