package org.example.kaisse.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.bson.Document;

public class MenuController {

    @FXML
    public GridPane form_popup;

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField imageField;

    @FXML
    protected void onDishButtonClick() {
        form_popup.setVisible(!form_popup.isVisible());
    }
    @FXML
    public void addDish() {
        try {
            String name = nameField.getText();
            String description = descriptionField.getText();
            float price = Float.parseFloat(priceField.getText());
            String image = imageField.getText();

            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("Kaisse");
            MongoCollection<Document> collection = database.getCollection("dish");

            Document dish = new Document("name", name)
                    .append("description", description)
                    .append("price", price)
                    .append("image", image);

            collection.insertOne(dish);
            System.out.println("✅ Plat ajouté avec succès !");
            mongoClient.close();

            // Reset des champs (optionnel)
            nameField.clear();
            descriptionField.clear();
            priceField.clear();
            imageField.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
