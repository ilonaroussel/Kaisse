package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.bson.Document;
import org.example.kaisse.Main;
import org.example.kaisse.model.Order;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FinancesController implements Initializable {
    @FXML Label winningsLabel;
    @FXML Label spendingLabel;
    @FXML Label benefitLabel;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        MongoCollection<Document> orderCollection = Main.database.getCollection("Order");

        List<Document> orderDocuments = orderCollection
                .find()
                .into(new ArrayList<>());

        List<Order> orders = orderDocuments
                .stream()
                .map(Order::createFromDocument)
                .filter(order -> order.getState().equals("VALIDATED"))
                .toList();

        double winnings = orders
                .stream()
                .map(order -> order
                        .getDishes()
                        .stream()
                        .map(orderDish -> orderDish.getQuantity() * orderDish.getDish().getPrice())
                        .reduce((double) 0, Double::sum))
                .reduce((double) 0, Double::sum);

        winningsLabel.setText(String.format("%.2f€", winnings));

        double spending = orders
                .stream()
                .map(order -> order.getDishes()
                        .stream()
                        .map(orderDish -> orderDish.getQuantity() * orderDish.getDish().getIngredients()
                                .stream()
                                .map(ingredient -> ingredient.getQuantity() * ingredient.getPrice())
                                .reduce((double) 0, Double::sum))
                        .reduce((double) 0, Double::sum))
                .reduce((double) 0, Double::sum);

        spendingLabel.setText(String.format("%.2f€", spending));

        double benefit = winnings - spending;

        benefitLabel.setText(String.format("%.2f€", benefit));
    }
}
