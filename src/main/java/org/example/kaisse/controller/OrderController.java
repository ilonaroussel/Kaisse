package org.example.kaisse.controller;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.*;
import com.mongodb.client.model.Sorts;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.kaisse.Main;
import org.example.kaisse.model.Chronometer;
import org.example.kaisse.model.Dish;
import org.example.kaisse.model.Order;
import org.example.kaisse.model.Table;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderController implements Initializable {
    @FXML private Label timerLabel;
    @FXML private Button ajouterTableButton;
    private Chronometer chronometer;

    @FXML ListView<VBox> orderList;
    private ObservableList<VBox> orderListItems;
    @FXML private ChoiceBox<Integer> tableNumber;
    @FXML ListView<HBox> dishList;

    private Order selectedOrder;
    private VBox selectedOrderVBox;

    final double width = 200;
    final ObservableList<String> states = FXCollections.observableArrayList(
            "CANCELED",
            "PENDING",
            "READY",
            "DELIVERED",
            "VALIDATED"
    );

    // Method that triggers on controller load
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        initOrderList();
        initTableChoiceBox();
        initDishList();
        initializeChronometer();
    }

    public void initializeChronometer() {
        chronometer = new Chronometer();
        chronometer.start(); // Démarrer le chronomètre

        // Mettre à jour le timer toutes les secondes
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> updateTimer())
        );

        timeline.setCycleCount(Timeline.INDEFINITE); // Répéter indéfiniment
        timeline.play();
    }

    private void updateTimer() {
        int remainingTime = chronometer.getRemainingTime(); // Récupère le temps restant
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;

        // Mettre à jour l'affichage du timer sur le label
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        // Désactiver le bouton après 10 minutes
        if (remainingTime <= 600) { // 600 secondes = 10 minutes
            ajouterTableButton.setDisable(true); // Désactive le bouton
        } else {
            ajouterTableButton.setDisable(false); // Si le temps est encore supérieur à 10 minutes, le bouton reste activé
        }
    }

    private void initOrderList() {
        orderListItems = FXCollections.observableArrayList();
        MongoCollection<Document> orderCollection = Main.database.getCollection("Order");

        List<Document> orderDocuments = orderCollection
                .find()
                .into(new ArrayList<>());

        orderDocuments
                .stream()
                .map(Order::createFromDocument)
                .filter(order -> {
                    String state = order.getState();

                    return state.equals("PENDING") || state.equals("READY") || state.equals("DELIVERED");
                })
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .forEach(this::addOrderToList);

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
        // Label with the total price
        Label priceLabel = new Label(String.format("%.2f€", order.getPrice()));

        ChoiceBox<String> stateChoice = new ChoiceBox<>(states);
        stateChoice.setValue(order.getState());
        stateChoice.setOnAction(_ -> {
            order.changeState(stateChoice.getValue());

            initOrderList();
        });

        // Add elements to the HBox
        hbox.getChildren().addAll(tableLabel, dateLabel, priceLabel, stateChoice);

        ListView<HBox> dishesList = new ListView<>();
        dishesList.setPrefHeight(100);
        dishesList.setPrefWidth(width);

        // Initialize ListView with existing dishes
        ObservableList<HBox> dishListItems = FXCollections.observableArrayList();
        order.getDishes()/*.stream()*/.forEach(orderDish -> {
            // Create a new HBox with the name and the price of the dish
            HBox box = new HBox(20);
            box.setPrefWidth(width);

            Label name = new Label(orderDish.getDish().getName() + " x " + orderDish.getQuantity());
            Label price = new Label(String.format("%.2f€", orderDish.getQuantity() * orderDish.getDish().getPrice()));

            Button deleteDishButton = new Button("❌");
            deleteDishButton.setOnAction(_ -> {
                order.removeDish(orderDish);

                ObservableList<HBox> items =  dishesList.getItems();
                items.remove(box);
                dishesList.setItems(items);
            });

            box.getChildren().addAll(name, price, deleteDishButton);

            dishListItems.add(box);
        });
        dishesList.setItems(dishListItems);

        // Add elements to the VBox
        vbox.getChildren().addAll(hbox, dishesList);

        // Add HBox to the ListView
        orderListItems.add(vbox);
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
        initOrderList(); // update orderList
    }

    private void addDishToOrder(Dish dish) {
        if (selectedOrderVBox == null) return;

        selectedOrder.addDish(dish); // update db
        initOrderList(); // update view
    }
}
