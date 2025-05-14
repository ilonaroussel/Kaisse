package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.example.kaisse.Main;
import org.example.kaisse.SceneManager;
import org.example.kaisse.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserListController {

    @FXML private GridPane form_popup;
    @FXML private ListView<HBox> usersList;
    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField jobField;
    @FXML private TextField workTimeField;
    @FXML private CheckBox isAdminCheckBox;

    @FXML protected void handleCreateUser(ActionEvent event) throws IOException {
        SceneManager.changeScene("create-user-view", event);
    }

    @FXML protected void onUserClick(User user) {
        Dialog<Void> formDialog = new Dialog<>();
        DialogPane dialogPane = formDialog.getDialogPane();
        formDialog.setTitle("Edit User");
        formDialog.setHeight(300);
        formDialog.setWidth(300);
        dialogPane.setContent(form_popup);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        nameField.setText(user.getName());
        jobField.setText(user.getJob());
        workTimeField.setText(user.getWorkTime().toString());
        isAdminCheckBox.setSelected(user.getAdmin());

        form_popup.setVisible(true);
        formDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String name = nameField.getText();
                String password = user.getPassword();

                if (!passwordField.getText().trim().isEmpty()) {
                    password = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());
                }

                String job = jobField.getText();
                Double workTime = Double.valueOf(workTimeField.getText());
                Boolean isAdmin = isAdminCheckBox.isSelected();
                editUser(user, name, password, job, workTime, isAdmin);
            }
            return null;
        });
        formDialog.showAndWait();
        form_popup.setVisible(false);
    }

    @FXML public void editUser(User user, String name, String password, String job, Double workTime, Boolean isAdmin) {

        MongoDatabase database = Main.database;
        MongoCollection<Document> collection = database.getCollection("User");

        Document editedUser = new Document("name", name)
                .append("password", password)
                .append("job", job)
                .append("workTime", workTime)
                .append("isAdmin", isAdmin);

        //Réinitialise les champs
        nameField.clear();
        passwordField.clear();
        jobField.clear();
        workTimeField.clear();

        try {
            collection.updateOne(Filters.eq("name", user.getName()), new Document("$set", editedUser));
            initialize();
            System.out.println("User Edited");
        } catch (Exception e) {
            System.out.println("Fail Edit: " + e);
        }
    };

    @FXML protected void initialize(){
        MongoDatabase database = Main.database;
        usersList.setItems(getAllUsers(database));

    }

    protected ObservableList<HBox> getAllUsers(MongoDatabase database){
        MongoCollection<Document> collection = database.getCollection("User");

        List<Document> documents = collection.find().into(new ArrayList<>());
        List<User> users = documents.stream().map(User::createFromDocument).toList();

        return FXCollections.observableArrayList(
                users.stream().map(user -> {
                    HBox userCard = new HBox(20);
                    userCard.setPrefHeight(50);
                    userCard.setPrefWidth(75);

                    Image image = new Image("https://picsum.photos/200");
                    ImageView userImage = new ImageView();
                    userImage.setFitHeight(50);
                    userImage.setFitWidth(75);
                    userImage.setImage(image);

                    Label userName = new Label(user.getName());
                    Label userJob = new Label(user.getJob());
                    Label userWorkTime = new Label(user.getWorkTime() + "h");

                    userCard.getChildren().addAll(userImage, userName, userJob, userWorkTime);

                    userCard.setOnMouseClicked(event -> onUserClick(user));

                    return userCard;
                }).toList());
    }
}
