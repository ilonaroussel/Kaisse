package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
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

public class CreateUserController {
    @FXML private TextField name;
    @FXML private TextField password;
    @FXML private TextField confirmPassword;
    @FXML private TextField age;
    @FXML private TextField job;

    @FXML protected void handleSubmit(ActionEvent event) throws IOException {
        MongoCollection<Document> collection = Main.database.getCollection("User");

        // Get the TextFiled data when the submit button is clicked
        String submitName = name.getText();
        String submitPassword = password.getText();
        String submitConfirmPassword = confirmPassword.getText();
        String submitAge = age.getText();
        String submitJob = job.getText();

        // Check and Throw an error if a field is empty
        if (submitName.trim().isEmpty() || submitPassword.trim().isEmpty() || submitConfirmPassword.trim().isEmpty()) {
            createDialog("Please fill in all the fields");

            return;
        }

        // Check and Throw an error if the name already exist
        Document doc = collection.find(new Document("name", submitName)).first();
        if (doc != null) {
            createDialog("A user with this name already exist");

            return;
        }

        // Check and Throw an if the confirmPassword doesn't match with the password
        if (!Objects.equals(submitPassword, submitConfirmPassword)) {
            createDialog("Password does not match");

            return;
        }

        int parsedSubmitAge;
        // Check and Throw an if the age isn't an Integer
        try {
            parsedSubmitAge = Integer.parseUnsignedInt(submitAge);
        } catch(NumberFormatException | NullPointerException e) {
            createDialog("Given age is not a valid number");

            return;
        }

        // Create a new User and prepare it to be sent to the db
        String hashedPassword = BCrypt.hashpw(submitPassword, BCrypt.gensalt());
        User user = new User(submitName, hashedPassword, submitJob, 0.0, false, parsedSubmitAge);

        Document userDoc = user.convertToDocument();

        // Send the new User to the db
        try {
            collection.insertOne(userDoc);
            System.out.println("Inserted: " + user);
        } catch (Exception e) {
            System.out.println("Fail Insert: " + e);
        }

        SceneManager.changeScene("main-view.fxml", event);
    }

    private void createDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Register Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML public void handleBack(MouseEvent event) throws IOException {
        SceneManager.changeScene("user-list-view.fxml", event);
    }
}