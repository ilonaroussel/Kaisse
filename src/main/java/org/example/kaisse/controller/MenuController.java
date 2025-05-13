package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.kaisse.Main;
import org.example.kaisse.model.Dish;

import javax.print.Doc;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MenuController {
    @FXML
    public GridPane form_popup;

    @FXML
    protected void onDishButtonClick(){
        form_popup.setVisible(!form_popup.isVisible());

    };

    @FXML
    protected void getAllDishesFromDatabase(){
        MongoDatabase database = Main.database;
        MongoCollection<Document> collection = database.getCollection("Dish");

        List<Document> documents = collection.find().into(new ArrayList<>());
        List<Dish> dishes = documents.stream().map(Dish::createFromDocument).toList();
        System.out.println(dishes);

    }
}
