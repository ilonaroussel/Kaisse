package org.example.kaisse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.example.kaisse.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ControlPanelView implements Initializable {
    @FXML GridPane grid;

    final List<String> cardLinks = List.of(
            "/org/example/kaisse/components/Delivered-orders-card.fxml",
            "/org/example/kaisse/components/Ready-orders-card.fxml",
            "/org/example/kaisse/components/Employes-filter-view.fxml",
            "/org/example/kaisse/components/Menu-control-panel.fxml",
            "/org/example/kaisse/components/Search-dish-card.fxml",
            "/org/example/kaisse/components/Order-winnings-card.fxml"
            );

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        for (int i = 0; i < 6; i++) {
            loadCard(cardLinks.get(i), i % 2, i / 2);
        }
    }

    @FXML protected void handleBack(ActionEvent event) throws IOException {
        SceneManager.changeScene("Main-view.fxml", event);
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
