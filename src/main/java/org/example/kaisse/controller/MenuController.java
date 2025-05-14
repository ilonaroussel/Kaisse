package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.example.kaisse.Main;
import org.example.kaisse.model.Dish;

import java.util.ArrayList;
import java.util.List;


public class MenuController {

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
//        form_popup.setVisible(!form_popup.isVisible());
        Dialog<Void> formDialog = new Dialog<>();
        DialogPane dialogPane = formDialog.getDialogPane();
        dialogPane.setContent(form_popup);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        form_popup.setVisible(true);
        formDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String name = nameField.getText();
                String description = descriptionField.getText();
                float price = Float.parseFloat(priceField.getText());
                String image = imageField.getText();
                addDish(name, description, price, image);
            }
            return null;
        });
        formDialog.showAndWait();
        form_popup.setVisible(false);
    }
    
    @FXML
    public void addDish(String name, String description, float price, String image) {

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
        dishList.setItems(getAllDishes(database));

    }

    protected ObservableList<HBox> getAllDishes(MongoDatabase database){
        MongoCollection<Document> collection = database.getCollection("Dish");

        List<Document> documents = collection.find().into(new ArrayList<>());
        List<Dish> dishes = documents.stream().map(Dish::createFromDocument).toList();

        return FXCollections.observableArrayList(
                dishes.stream().map(dish -> {
                    HBox dishCard = new HBox(20);
                    dishCard.setPrefHeight(50);
                    dishCard.setPrefWidth(75);

                    Image image = new Image(dish.getImage());
                    ImageView dishImage = new ImageView();
                    dishImage.setFitHeight(50);
                    dishImage.setFitWidth(75);
                    dishImage.setImage(image);

                    Label dishName = new Label(dish.getName());
                    Label dishPrice = new Label(dish.getPrice() + "€");
                    Label dishDescription = new Label(dish.getDescription());

                    dishCard.getChildren().addAll(dishImage, dishName, dishPrice, dishDescription);

                    return dishCard;
                }).toList());
    }
}
