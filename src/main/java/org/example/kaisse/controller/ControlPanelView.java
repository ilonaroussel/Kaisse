package org.example.kaisse.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControlPanelView implements Initializable {
    @FXML GridPane grid;

    final List<String> cardLinks = List.of(
            "/org/example/kaisse/components/delivered-orders-card.fxml",
            "/org/example/kaisse/components/ready-orders-card.fxml",
            "/org/example/kaisse/components/employes-filter-view.fxml",
            "/org/example/kaisse/components/menu-control-panel.fxml",
            "/org/example/kaisse/components/search-dish-card.fxml",
            "/org/example/kaisse/components/order-winnings-card.fxml"
            );

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 0; i < 6; i++) {
            loadCard(cardLinks.get(i), i % 2, i / 2);
        }
    }

    private void loadCard(String link, int x, int y) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(link));

        try {
            AnchorPane cardContent = loader.load();
            grid.add(cardContent, x, y);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
