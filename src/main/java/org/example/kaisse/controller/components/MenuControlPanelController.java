package org.example.kaisse.controller.components;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.bson.Document;
import org.example.kaisse.Main;
import org.example.kaisse.model.Dish;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MenuControlPanelController {
    @FXML private VBox cheaperDish;
    @FXML private VBox mostExpansiveDish;
    @FXML private VBox totalMenuCost;

    MongoDatabase database = Main.database;

    @FXML
    protected void initialize(){
        getCheaperDish();
        getMostExpansiveDish();
        getTotalMenuCost();
    }

    @FXML
    protected void getCheaperDish(){
        MongoCollection<Document> collection = database.getCollection("Dish");

        List<Document> documents = collection.find().into(new ArrayList<>());
        List<Dish> dishes = documents.stream().map(Dish::createFromDocument).toList();


        Dish cheapDish = dishes.stream().min(Comparator.comparing(Dish::getPrice)).orElseThrow(() -> new IllegalStateException("Aucun plat trouvé dans la base de données."));
        Label dishName = new Label(cheapDish.getName());
        Label dishPrice = new Label(String.format("%.2f€", cheapDish.getPrice()));

        cheaperDish.getChildren().addAll(dishName,dishPrice);
    }

    @FXML
    protected void getMostExpansiveDish(){
        MongoCollection<Document> collection = database.getCollection("Dish");

        List<Document> documents = collection.find().into(new ArrayList<>());
        List<Dish> dishes = documents.stream().map(Dish::createFromDocument).toList();

        Dish expansiveDish = dishes.stream().max(Comparator.comparing(Dish::getPrice)).orElseThrow(() -> new IllegalStateException("Aucun plat trouvé dans la base de données."));
        Label dishName = new Label(expansiveDish.getName());
        Label dishPrice = new Label(String.format("%.2f€", expansiveDish.getPrice()));

        mostExpansiveDish.getChildren().addAll(dishName,dishPrice);
    }

    @FXML
    protected void getTotalMenuCost(){
        MongoCollection<Document> collection = database.getCollection("Dish");

        List<Document> documents = collection.find().into(new ArrayList<>());
        List<Dish> dishes = documents.stream().map(Dish::createFromDocument).toList();


        Double totalMenu = dishes.stream().reduce(0.0 , (sum,d) -> sum + d.getPrice() ,Double::sum);

        Label menuCost = new Label(String.format("%.2f€", totalMenu));

        totalMenuCost.getChildren().add(menuCost);
    }
}
