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
        SceneManager.changeScene("Login-view.fxml", event);
    }

    // Navigation between all pages
    @FXML protected void goToUserList(ActionEvent event) throws IOException  {
        SceneManager.changeScene("User-list-view.fxml", event);
    }
    @FXML protected void goToFinances(ActionEvent event) throws IOException  {
        SceneManager.changeScene("Finances-view.fxml", event);
    }
    @FXML protected void goToOrder(ActionEvent event) throws IOException  {
        SceneManager.changeScene("Order-view.fxml", event);
    }
    @FXML protected void goToMenu(ActionEvent event) throws IOException  {
        SceneManager.changeScene("Menu-view.fxml", event);
    }
    @FXML protected void goToTable(ActionEvent event) throws IOException  {
        SceneManager.changeScene("Table-view.fxml", event);
    }
    @FXML protected void goToPanel(ActionEvent event) throws IOException  {
        SceneManager.changeScene("Control-panel-view.fxml", event);
    }


    @Override public void initialize(URL location, ResourceBundle resources) {
        if (Main.loggedUser != null) {
            loggedUserName.setText("Bienvenue " + Main.loggedUser.getName());

            // If the user is admin display the button
            Boolean isAdmin = Main.loggedUser.getAdmin();
            if (isAdmin) {
                userListButton.setVisible(true);
                userListButton.setManaged(true);
            }
        }
    }
}