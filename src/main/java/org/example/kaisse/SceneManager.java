package org.example.kaisse;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {
    public static void changeScene(String view, Event event) throws IOException {
        // Take the name of the view in param and create a full path
        String viewPath = "/org/example/kaisse/";
        String fullPath = viewPath.concat(view);

        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fullPath)));

        // Stretch the size of the display for taking all the screen.
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        // Add the css on the new scene
        scene.getStylesheets().add(
                Objects.requireNonNull(SceneManager.class.getResource("style/global.css")).toExternalForm()
        );

        // Launch it
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}

