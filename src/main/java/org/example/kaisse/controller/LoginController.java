package org.example.kaisse.controller;

import com.mongodb.client.FindIterable;
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

import static com.mongodb.client.model.Filters.eq;

public class LoginController {
    @FXML private TextField name;
    @FXML private TextField password;

    @FXML protected void handleSubmit(ActionEvent event) throws IOException {

        MongoDatabase database = Main.database;
        MongoCollection<Document> collection = database.getCollection("User");

        String submitName = name.getText();
        String submitPassword = password.getText();
        Document doc = collection.find(new Document("name", submitName)).first();

        if (doc == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("User not found");
            alert.showAndWait();
            return;
        }

        User user = new User(doc.getString("name"), doc.getString("password"), doc.getString("job"), doc.getDouble("workTime"));

        String userPassword = user.getPassword();

        if (!BCrypt.checkpw(submitPassword, userPassword)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Error");
            alert.setHeaderText(null);
            alert.setContentText("Password does not match");
            alert.showAndWait();
            return;
        }

        System.out.println(user.getName());
        Main.logedUser = user;

        SceneManager.changeScene("main-view", event);

    }
}