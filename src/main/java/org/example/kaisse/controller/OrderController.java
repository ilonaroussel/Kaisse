package org.example.kaisse.controller;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.kaisse.Main;
import org.example.kaisse.model.Order;
import org.example.kaisse.model.Table;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.mongodb.client.model.Aggregates.lookup;

public class OrderController implements Initializable {
    @FXML
    ListView<HBox> orderList;
    @FXML
    private TextField tableNumber;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        MongoCollection<Document> collection = Main.database.getCollection("Order");

        List<Bson> pipeline = Arrays.asList(
                match(eq("state", "PENDING")),
                lookup("Table", "table", "_id", "table"),
                lookup("Dish", "dishes", "_id", "dishes"));

        List<Document> documents = collection.aggregate(pipeline).into(new ArrayList<>());

        ObservableList<HBox> items = FXCollections.observableArrayList();

        documents.stream().forEach(element -> {
            Order order = Order.createFromDocument(element);

            HBox box = new HBox(20);
            box.setPrefHeight(100);
            box.setPrefWidth(200);

            Label tableLabel = new Label(order.getTable().getNumber().toString());
            Label dateLabel = new Label(order.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

            box.getChildren().addAll(tableLabel, dateLabel);

            items.add(box);
        });

        orderList.setItems(items);
    }

    @FXML
    public void createOrder() {
        MongoCollection<Document> collection = Main.database.getCollection("Table");

        Document document = collection.find(eq("number", tableNumber.getText())).first();
        assert document != null;

        Table table = Table.createFromDocument(document);

        System.out.println(document);

        new Order("PENDING", table, LocalDateTime.now(), new ArrayList<>()).insertToDb();
    }
}
