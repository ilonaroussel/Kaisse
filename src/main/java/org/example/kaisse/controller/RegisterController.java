package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.bson.Document;
import org.example.kaisse.SceneManager;
import org.example.kaisse.Main;
import org.example.kaisse.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Objects;

public class RegisterController {
    @FXML private TextField name;
    @FXML private TextField password;
    @FXML private TextField confirmPassword;

    @FXML protected void handleSubmit(ActionEvent event) throws IOException {

        MongoDatabase database = Main.database;
        MongoCollection<Document> collection = database.getCollection("User");

        // Get the TextFiled data when the submit button is clicked
        String submitName = name.getText();
        String submitPassword = password.getText();
        String submitConfirmPassword = confirmPassword.getText();

        // Check and Throw an error if a field is empty
        if (submitName.trim().isEmpty() || submitPassword.trim().isEmpty() || submitConfirmPassword.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Field Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the fields");
            alert.showAndWait();
            return;
        }

        // Check and Throw an error if the name already exist
        Document doc = collection.find(new Document("name", submitName)).first();
        if (doc != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Register Error");
            alert.setHeaderText(null);
            alert.setContentText("A user with this name already exist");
            alert.showAndWait();
            return;
        }

        // Check and Throw an if the confirmPassword doesn't match with the password
        if (!Objects.equals(submitPassword, submitConfirmPassword)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Register Error");
            alert.setHeaderText(null);
            alert.setContentText("Password does not match");
            alert.showAndWait();
            return;
        }

        // Create a new User and prepare it to be sent to the db
        String hashedPassword = BCrypt.hashpw(submitPassword, BCrypt.gensalt());
        User user = new User(submitName, hashedPassword, "", 0.0, false);

        Document userDoc = new Document("name", user.getName())
                .append("password", user.getPassword())
                .append("job", user.getJob())
                .append("workTime", user.getWorkTime())
                .append("isAdmin", false);

        // Send the new User to the db
        try {
            collection.insertOne(userDoc);
            System.out.println("Inserted: " + user);
        } catch (Exception e) {
            System.out.println("Fail Insert: " + e);
        }

        SceneManager.changeScene("main-view", event);

    }

    @FXML public void handleLoginClick(MouseEvent event) throws IOException {
        SceneManager.changeScene("login-view", event);
    }
}