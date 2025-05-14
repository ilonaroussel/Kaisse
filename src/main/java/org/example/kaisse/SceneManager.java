package org.example.kaisse;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {
    public static void changeScene(String view, Event event) throws IOException {
        String viewPath = "/org/example/kaisse/";
        String fullPath = viewPath.concat(view);
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fullPath)));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}

