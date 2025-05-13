package org.example.kaisse;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bson.Document;
import org.example.kaisse.controller.OrderController;

import java.io.IOException;

public class Main extends Application {
    public static MongoClient mongoClient;
    public static MongoDatabase database;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("order-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
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