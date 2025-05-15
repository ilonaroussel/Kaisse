package org.example.kaisse.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.kaisse.Main;
import org.example.kaisse.SceneManager;
import org.example.kaisse.model.User;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private Label loggedUserName;

    @FXML private Button userListButton;

    @FXML protected void handleLogout(ActionEvent event) throws IOException  {
        Main.loggedUser = null;
        SceneManager.changeScene("login-view.fxml", event);
    }

    @FXML protected void goToUserList(ActionEvent event) throws IOException  {
        SceneManager.changeScene("user-list-view.fxml", event);
    }

    @Override public void initialize(URL location, ResourceBundle resources) {
        if (Main.loggedUser != null) {
            loggedUserName.setText("Bienvenue " + Main.loggedUser.getName());

            Boolean isAdmin = Main.loggedUser.getAdmin();
            if (isAdmin) {
                userListButton.setVisible(true);
            }
        }
    }

}