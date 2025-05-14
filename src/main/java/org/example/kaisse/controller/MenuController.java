package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.kaisse.Main;
import org.example.kaisse.model.Dish;

import javax.print.Doc;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class MenuController {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @FXML
    private GridPane form_popup;
    @FXML
    private ListView<HBox> dishList;
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField imageField;

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
    };

    @FXML
    protected void initialize(){
        MongoDatabase database = Main.database;
        MongoCollection<Document> collection = database.getCollection("Dish");

        List<Document> documents = collection.find().into(new ArrayList<>());
        List<Dish> dishes = documents.stream().map(Dish::createFromDocument).toList();

        ObservableList<HBox> dishesListView = FXCollections.observableArrayList(
                dishes.stream().map(dish -> {
            HBox dishCard = new HBox(20);
            dishCard.setPrefHeight(100);
            dishCard.setPrefWidth(200);

            Image image = new Image(dish.getImage());
            ImageView dishImage = new ImageView();
            dishImage.setImage(image);

            Label dishName = new Label(dish.getName());
            Label dishPrice = new Label(df.format(dish.getPrice()) + "€");
            Label dishDescription = new Label(dish.getDescription());

            dishCard.getChildren().addAll(dishImage, dishName, dishPrice, dishDescription);

            return dishCard;
        }).toList());

        dishList.setItems(dishesListView);
    }
}
