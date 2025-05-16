package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.bson.Document;
import org.example.kaisse.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.kaisse.SceneManager;
import org.example.kaisse.model.Table;

public class TableController {

    @FXML
    private ListView<String> listTable;
    @FXML
    private TextField numberField;
    @FXML
    private TextField seatsField;
    @FXML
    private TextField emplacementField;

    //Return to home page
    @FXML protected void handleBack(ActionEvent event) throws IOException {
        SceneManager.changeScene("Main-view.fxml", event);
    }

    @FXML
    protected void getAllTablesFromDatabase() {
        try {
            MongoDatabase database = Main.database;
            MongoCollection<Document> collection = database.getCollection("Table");

            List<Document> documents = collection.find().into(new ArrayList<>());

            List<Table> tables = documents.stream()
                    .map(Table::createFromDocument)
                    .toList();

            ObservableList<String> tableDisplayList = FXCollections.observableArrayList();

            for (Table table : tables) {
                String display = "Table " + table.getNumber() +
                        " | Places : " + table.getSeats() +
                        " | Emplacement : " + table.getEmplacement() +
                        " | Libre : " + (table.getFree() ? "Disponible" : "Indisponible");
                tableDisplayList.add(display);
            }

            listTable.setItems(tableDisplayList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addTable() {
        try {
            //Retrieves values from textfield
            Integer number = Integer.parseInt(numberField.getText());
            Integer seats = Integer.parseInt(seatsField.getText());
            String emplacement = emplacementField.getText();

            if (emplacement.isEmpty()) {
                return;
            }

            MongoDatabase database = Main.database;
            MongoCollection<Document> collection = database.getCollection("Table");

            Document dish = new Document("number", number)
                    .append("seats", seats)
                    .append("emplacement", emplacement)
                    .append("isFree", false);

            collection.insertOne(dish);

            numberField.clear();
            seatsField.clear();
            emplacementField.clear();

            getAllTablesFromDatabase(); //Refreshes the display

        } catch (NumberFormatException e) {
            System.out.println("Entrez des nombres valides pour le numéro et les places.");
        } catch (Exception e) {
            System.out.println("Erreur d'insertion : " + e.getMessage());
        }
    };

    @FXML
    public void initialize() {
        getAllTablesFromDatabase();
    }
}
