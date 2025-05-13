package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import java.util.Objects;

public class RegisterController {
    @FXML private TextField name;
    @FXML private TextField password;
    @FXML private TextField confirmPassword;

    @FXML protected void handleSubmit(ActionEvent event) throws IOException {

        String submitName = name.getText();
        String submitPassword = password.getText();
        String submitConfirmPassword = confirmPassword.getText();

        if (!Objects.equals(submitPassword, submitConfirmPassword)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Password does not match");
            alert.showAndWait();
            return;
        }
        String hashedPassword = BCrypt.hashpw(submitPassword, BCrypt.gensalt());
        User userTest = new User(submitName, hashedPassword, "", 0.0);

        MongoDatabase database = Main.database;
        MongoCollection<Document> collection = database.getCollection("User");

        Document userDoc = new Document("name", userTest.getName())
                .append("password", userTest.getPassword())
                .append("job", userTest.getJob())
                .append("workTime", userTest.getWorkTime());

        try {
            collection.insertOne(userDoc);
            System.out.println("Inserted: " + userTest);
        } catch (Exception e) {
            System.out.println("Fail Insert: " + e);
        }

        SceneManager.changeScene("main-view", event);

    }
}