package org.example.kaisse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.kaisse.SceneManager;

import java.io.IOException;

public class RegisterController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void handleSubmit(ActionEvent event) throws IOException {
        SceneManager.changeScene("main-view", event);

    }
}