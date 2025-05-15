package org.example.kaisse.controller.components;

import com.mongodb.client.MongoCollection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.example.kaisse.Main;
import org.example.kaisse.model.Order;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.mongodb.client.model.Filters.eq;

public class ReadyOrdersController implements Initializable {
    @FXML ListView<HBox> deliveredOrdersList;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<HBox> orderListItems = FXCollections.observableArrayList();
        MongoCollection<Document> collection = Main.database.getCollection("Order");

        List<Document> documents = collection.find(eq("state", "READY")).into(new ArrayList<>());

        documents
                .stream()
                .map(Order::createFromDocument)
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .forEach(order -> orderListItems.add(createOrderRow(order)));

        deliveredOrdersList.setItems(orderListItems);
    }

    private HBox createOrderRow(Order order) {
        HBox box = new HBox(10);

        Label table = new Label(order.getTable().getNumber().toString());
        Label date = new Label(order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Label price = new Label(String.format("%.2f€", order
                .getDishes()
                .stream()
                .map(orderDish -> orderDish.getQuantity() * orderDish.getDish().getPrice())
                .reduce(0.0, Double::sum)
        ));

        box.getChildren().addAll(table, date, price);

        return box;
    }
}
