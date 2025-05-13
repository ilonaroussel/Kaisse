package org.example.kaisse.controller;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
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
    private ChoiceBox<Integer> tableNumber;

    // Dynamic list to update the view
    private final ObservableList<HBox> orderListItems = FXCollections.observableArrayList();

    // Method that triggers on controller load
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Init the ListView of orders
        MongoCollection<Document> orderCollection = Main.database.getCollection("Order");

        // Filters and foreign keys matching
        List<Bson> pipeline = Arrays.asList(
                match(eq("state", "PENDING")),
                lookup("Table", "table", "_id", "table"),
                lookup("Dish", "dishes", "_id", "dishes"));

        List<Document> orderDocuments = orderCollection.aggregate(pipeline).into(new ArrayList<>());

        orderDocuments.stream().forEach(element -> {
            // Converts Document to Order
            Order order = Order.createFromDocument(element);

            addOrderInList(order);
        });

        orderList.setItems(orderListItems);

        // Init the choices of the ChoiceBox
        MongoCollection<Document> tableCollection = Main.database.getCollection("Table");

        ObservableList<Integer> tableNumbers = FXCollections.observableArrayList();
        // Get all the tables in database
        List<Document> tableDocuments = tableCollection.find().into(new ArrayList<>());

        // Create a choice for each table
        tableDocuments.stream().forEach(element -> tableNumbers.add(element.getInteger("number")));

        tableNumber.setItems(tableNumbers);
    }

    private void addOrderInList(Order order) {
        // HBox to align items horizontally
        HBox box = new HBox(20);
        box.setPrefHeight(100);
        box.setPrefWidth(200);

        // Label with the table number
        Label tableLabel = new Label(order.getTable().getNumber().toString());
        // Label with the formatted date
        Label dateLabel = new Label(order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(actionEvent -> {
            order.deleteFromDb();
            orderListItems.remove(box);
        });

        Button validateButton = new Button("✅");
        validateButton.setOnAction(actionEvent -> {
            order.validate();
            orderListItems.remove(box);
        });

        // Add elements to the HBox
        box.getChildren().addAll(tableLabel, dateLabel, deleteButton, validateButton);

        // Add HBox to the ListView
        orderListItems.add(box);
    }

    @FXML
    public void createOrder() {
        Integer tableNumberValue = tableNumber.getValue();
        // if no table is chosen
        if (tableNumberValue == null) {
            throw new IllegalArgumentException("No table selected");
        }

        MongoCollection<Document> tableCollection = Main.database.getCollection("Table");

        Document document = tableCollection.find(eq("number", tableNumberValue)).first();
        if (document == null) {
            throw new IllegalStateException("Table not found in database");
        }

        // Converts the Document to a Table
        Table table = Table.createFromDocument(document);

        MongoCollection<Document> orderCollection = Main.database.getCollection("Order");

        // if an order on the table is already ongoing
        if (orderCollection
                .find(and(eq("table", table.getId()), eq("state", "PENDING")))
                .first() != null) {
            throw new IllegalStateException("An order for this table is already ongoing");
        }

        // create a PENDING order
        Order order = new Order(ObjectId.get(), "PENDING", table, LocalDateTime.now(), new ArrayList<>());

        order.insertToDb(); // update db
        addOrderInList(order); // update orderList
    }
}
