package org.example.kaisse;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.kaisse.model.User;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static User loggedUser;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        scene.getStylesheets().add(
                Objects.requireNonNull(Main.class.getResource("style/global.css")).toExternalForm()
        );

        stage.setTitle("Kaisse!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        String uri = dotenv.get("DATABASE_URI");

        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("Kaisse");

        launch();
    }
}