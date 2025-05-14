package org.example.kaisse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.example.kaisse.Main;
import org.example.kaisse.SceneManager;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Label loggedUserName;

    @FXML
    protected void onHelloButtonClick(ActionEvent event) throws IOException  {
        SceneManager.changeScene("login-view.fxml", event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Main.loggedUser != null) {
            loggedUserName.setText("Bienvenue " + Main.loggedUser.getName());
        }
    }

}