package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.bson.Document;
import org.example.kaisse.SceneManager;
import org.example.kaisse.Main;
import org.example.kaisse.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

public class LoginController {
    @FXML private TextField name;
    @FXML private TextField password;

    @FXML protected void handleSubmit(ActionEvent event) throws IOException {
        MongoCollection<Document> collection = Main.database.getCollection("User");

        // Get the TextFiled data when the submit button is clicked
        String submitName = name.getText();
        String submitPassword = password.getText();

        // Check and Throw an error if a field is empty
        if (submitName.trim().isEmpty() || submitPassword.trim().isEmpty()) {
            createDialog("Please fill in all the fields.");

            return;
        }

        // Check and Throw an error if the name doesn't exist
        Document doc = collection.find(eq("name", submitName)).first();
        if (doc == null) {
            createDialog("User not found");

            return;
        }

        // Get the infos of the user and put it into a new User
        User user = User.createFromDocument(doc);

        // Check and throw an error if the password doesn't match
        if (!BCrypt.checkpw(submitPassword, user.getPassword())) {
            createDialog("Password does not match");

            return;
        }

        Main.loggedUser = user;

        SceneManager.changeScene("main-view", event);
    }

    private void createDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}