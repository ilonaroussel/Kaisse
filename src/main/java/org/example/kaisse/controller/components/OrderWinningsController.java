package org.example.kaisse.controller.components;

import com.mongodb.client.MongoCollection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.bson.Document;
import org.example.kaisse.Main;
import org.example.kaisse.model.Order;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.not;

public class OrderWinningsController implements Initializable {
    @FXML Label winningsInBecomingLabel;
    @FXML Label winningsLabel;
    @FXML Label totalWinningsLabel;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        MongoCollection<Document> collection = Main.database.getCollection("Order");

        List<Document> documents = collection.find(not(eq("state", "CANCELED"))).into(new ArrayList<>());

        List<Order> todayOrders = documents
                .stream()
                .map(Order::createFromDocument)
                .filter(order -> order.getDate().toLocalDate().equals(LocalDate.now()))
                .toList();

        double winningsInBecoming = todayOrders
                .stream()
                .filter(order -> {
                    String state = order.getState();
                    return state.equals("PENDING") || state.equals("READY") || state.equals("DELIVERED");
                })
                .map(Order::getPrice)
                .reduce(0.0, Double::sum);


        winningsInBecomingLabel.setText(String.format("%.2f€", winningsInBecoming));

        double winnings = todayOrders
                .stream()
                .filter(order -> order.getState().equals("VALIDATED"))
                .map(Order::getPrice)
                .reduce(0.0, Double::sum);

        winningsLabel.setText(String.format("%.2f€", winnings));

        double totalWinnings = winningsInBecoming + winnings;

        totalWinningsLabel.setText(String.format("%.2f€", totalWinnings));
    }
}
