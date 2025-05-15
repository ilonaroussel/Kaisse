package org.example.kaisse.controller.components;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.example.kaisse.Main;
import org.example.kaisse.model.Dish;
import org.example.kaisse.model.Ingredient;

import java.util.*;

public class SearchDishController {

    @FXML private ListView<HBox> searchResults;
    @FXML private TextField searchBar;

    private final MongoDatabase database = Main.database;

    //Function that will search all the dish containing all the ingredients prompted by user
    @FXML
    protected void onActionSearchBar (){
        //get all Dish from the dish table
        MongoCollection<Document> collection = database.getCollection("Dish");
        List<Document> documents = collection.find().into(new ArrayList<>());
        List<Dish> dishes = documents.stream().map(Dish::createFromDocument).toList();

        //get ingredients prompted
        List<String> searchedIngredient = Arrays.stream(searchBar.getText().split(" ")).map(String::toLowerCase).toList();

        //filter dish and keeping only those wich have all ingredients in it
        List<Dish> filteredDishes = dishes
                .stream()
                .filter(dish -> {
                    List<String> ingredientsNames = dish.getIngredients().stream().map(ingredient -> ingredient.getName().toLowerCase()).toList();

                    return new HashSet<>(ingredientsNames).containsAll(searchedIngredient);
                })
                .toList();

        //Setup all the box to display dishes
        ObservableList<HBox> items = FXCollections.observableArrayList(
            filteredDishes.stream().map(dish -> {
                HBox dishCard = new HBox(20);
                dishCard.setMinHeight(50);
                dishCard.setMinWidth(75);

                Image image = new Image(dish.getImage());
                ImageView dishImage = new ImageView();
                dishImage.setFitHeight(50);
                dishImage.setFitWidth(75);
                dishImage.setImage(image);

                Label dishName = new Label(dish.getName());
                Label dishPrice = new Label(String.format("%.2f€", dish.getPrice()));


                dishCard.getChildren().addAll(dishImage, dishName, dishPrice);

                return  dishCard;
            }).toList()
        );

        searchResults.setItems(items);

    }
}
