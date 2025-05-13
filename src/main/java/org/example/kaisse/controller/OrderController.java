package org.example.kaisse.controller;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
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
    private ChoiceBox<String> tableNumber;

    private final ObservableList<HBox> orderListItems = FXCollections.observableArrayList();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Init the ListView of orders
        MongoCollection<Document> orderCollection = Main.database.getCollection("Order");

        List<Bson> pipeline = Arrays.asList(
                match(eq("state", "PENDING")),
                lookup("Table", "table", "_id", "table"),
                lookup("Dish", "dishes", "_id", "dishes"));

        List<Document> orderDocuments = orderCollection.aggregate(pipeline).into(new ArrayList<>());

        orderDocuments.stream().forEach(element -> {
            Order order = Order.createFromDocument(element);

            addOrderInList(order);
        });

        orderList.setItems(orderListItems);

        // Init the choices of the ChoiceBox
        MongoCollection<Document> tableCollection = Main.database.getCollection("Table");

        ObservableList<String> tableNumbers = FXCollections.observableArrayList();
        List<Document> tableDocuments = tableCollection.find().into(new ArrayList<>());

        tableDocuments.stream().forEach(element -> tableNumbers.add(element.getString("number")));

        tableNumber.setItems(tableNumbers);
    }

    private void addOrderInList(Order order) {
        HBox box = new HBox(20);
        box.setPrefHeight(100);
        box.setPrefWidth(200);

        Label tableLabel = new Label(order.getTable().getNumber().toString());
        Label dateLabel = new Label(order.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

        box.getChildren().addAll(tableLabel, dateLabel);

        orderListItems.add(box);
    }

    @FXML
    public void createOrder() {
        String tableNumberValue = tableNumber.getValue();
        // if no table chosen
        if (tableNumberValue == null) {
            throw new IllegalArgumentException("No table selected");
        }

        System.out.println(tableNumberValue);

        MongoCollection<Document> tableCollection = Main.database.getCollection("Table");

        Document document = tableCollection.find(eq("number", tableNumberValue)).first();
        if (document == null) {
            throw new IllegalStateException("Table not found in database");
        }

        Table table = Table.createFromDocument(document);

        MongoCollection<Document> orderCollection = Main.database.getCollection("Order");

        // if an order on the table is already ongoing
        if (orderCollection.find(eq("table", table.getId())).first() != null) {
            throw new IllegalStateException("An order for this table is already ongoing");
        }

        // create a PENDING order
        Order order = new Order("PENDING", table, LocalDateTime.now(), new ArrayList<>());

        order.insertToDb(); // update db
        addOrderInList(order); // update orderList
    }
}
