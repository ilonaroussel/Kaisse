package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.bson.Document;
import org.example.kaisse.Main;

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
            String name = nameField.getText();
            String description = descriptionField.getText();
            float price = Float.parseFloat(priceField.getText());
            String image = imageField.getText();

            MongoDatabase database = Main.database;
            MongoCollection<Document> collection = database.getCollection("Dish");

            Document dish = new Document("name", name)
                    .append("description", description)
                    .append("price", price)
                    .append("image", image);

            //Réinitialise les champs
            nameField.clear();
            descriptionField.clear();
            priceField.clear();
            imageField.clear();

        try {
            collection.insertOne(dish);
            System.out.println("Plat ajouté");
        } catch (Exception e) {
            System.out.println("Fail Insert: " + e);
        }
    }


}
