package org.example.kaisse.controller;

import  static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;

import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.example.kaisse.Main;
import org.example.kaisse.model.Dish;
import org.example.kaisse.model.Order;
import org.example.kaisse.model.Table;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderController implements Initializable {
    @FXML ListView<VBox> orderList;
    private final ObservableList<VBox> orderListItems = FXCollections.observableArrayList();

    @FXML private ChoiceBox<Integer> tableNumber;

    @FXML ListView<HBox> dishList;

    private Order selectedOrder;
    private VBox selectedOrderVBox;
    final double width = 200;

    // Method that triggers on controller load
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        initOrderList();
        initTableChoiceBox();
        initDishList();
    }

    private void initOrderList() {
        MongoCollection<Document> orderCollection = Main.database.getCollection("Order");

        /*
         * Pipeline to format the obtained data as follows:
         * - Get only the PENDING orders
         * - The sort is ascending because the elements are added at the start of the ListView,
         * so the oldest will be at the top of the list
         * - Find the foreign keys and get all their fields instead of just the ObjectId
         * */
        List<Bson> pipeline = Arrays.asList(
                match(eq("state", "PENDING")),
                sort(ascending("date")),
                lookup("Table", "table", "_id", "table"),
                lookup("Dish", "dishes", "_id", "dishes"));

        List<Document> orderDocuments = orderCollection
                .aggregate(pipeline)
                .into(new ArrayList<>());

        orderDocuments/*.stream()*/.forEach(element -> {
            // Converts Document to Order
            Order order = Order.createFromDocument(element);

            addOrderToList(order);
        });

        orderList.setItems(orderListItems);
    }

    private void initTableChoiceBox() {
        MongoCollection<Document> tableCollection = Main.database.getCollection("Table");

        ObservableList<Integer> tableNumbers = FXCollections.observableArrayList();
        // Get all the tables in database
        List<Document> tableDocuments = tableCollection
                .find()
                .sort(Sorts.ascending("number"))
                .into(new ArrayList<>());

        // Create a choice for each table
        tableDocuments/*.stream()*/.forEach(element -> tableNumbers.add(element.getInteger("number")));

        tableNumber.setItems(tableNumbers);
    }

    private void initDishList() {
        MongoCollection<Document> collection = Main.database.getCollection("Dish");

        List<Document> documents = collection
                .find()
                .into(new ArrayList<>());

        ObservableList<HBox> items = FXCollections.observableArrayList();

        documents/*.stream()*/.forEach(element -> {
            // Converts Document to Dish
            Dish dish = Dish.createFromDocument(element);

            HBox hbox = new HBox(20);
            hbox.setPrefHeight(100);
            hbox.setPrefWidth(width);
            hbox.setOnMouseClicked(_ -> addDishToOrder(dish));

            // Label with the table number
            Label dishName = new Label(dish.getName());
            // Label with the formatted date
            Label dishPrice = new Label(String.format("%.2f€", dish.getPrice()));

            hbox.getChildren().addAll(dishName, dishPrice);

            items.add(hbox);
        });

        dishList.setItems(items);
    }

    private void addOrderToList(Order order) {
        // VBox with details on top and dishes on bottom
        VBox vbox = new VBox();
        vbox.setPrefWidth(width);
        vbox.setOnMouseClicked(_ -> {
            selectedOrder = order;
            selectedOrderVBox = vbox;
        });

        // HBox with table data
        HBox hbox = new HBox(20);
        hbox.setPrefWidth(width);

        // Label with the table number
        Label tableLabel = new Label(order.getTable().getNumber().toString());
        // Label with the formatted date
        Label dateLabel = new Label(order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(_ -> {
            order.cancel();
            orderListItems.remove(vbox);
        });

        Button validateButton = new Button("✅");
        validateButton.setOnAction(_ -> {
            order.validate();
            orderListItems.remove(vbox);
        });

        // Add elements to the HBox
        hbox.getChildren().addAll(tableLabel, dateLabel, deleteButton, validateButton);

        ListView<HBox> dishesList = new ListView<>();
        dishesList.setPrefHeight(100);
        dishesList.setPrefWidth(width);

        // Initialize ListView with existing dishes
        ObservableList<HBox> dishListItems = FXCollections.observableArrayList();
        order.getDishes()/*.stream()*/.forEach(dish -> dishListItems.add(createDishRow(dish, order, dishesList)));
        dishesList.setItems(dishListItems);

        // Add elements to the VBox
        vbox.getChildren().addAll(hbox, dishesList);

        // Add HBox to the ListView
        orderListItems.addFirst(vbox);
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
        addOrderToList(order); // update orderList
    }

    private void addDishToOrder(Dish dish) {
        if (selectedOrderVBox == null) return;

        ListView<HBox> list = (ListView<HBox>) selectedOrderVBox
                .getChildren() // contains an HBox and a ListView
                .get(1); // get the ListView

        // Update database
        selectedOrder.addDish(dish);

        // Add the HBox to the ListView
        ObservableList<HBox> items = list.getItems();
        items.add(createDishRow(dish, selectedOrder, list));

        list.setItems(items);
    }

    public HBox createDishRow(Dish dish, Order order, ListView<HBox> list) {
        // Create a new HBox with the name and the price of the dish
        HBox box = new HBox(20);
        box.setPrefWidth(width);

        Label name = new Label(dish.getName());
        Label price = new Label(String.format("%.2f€", dish.getPrice()));

        Button deleteButton = new Button("❌");
        deleteButton.setOnAction(_ -> {
            order.removeDish(dish);

            ObservableList<HBox> items =  list.getItems();
            items.remove(box);
            list.setItems(items);
        });

        box.getChildren().addAll(name, price, deleteButton);

        return box;
    }
}
