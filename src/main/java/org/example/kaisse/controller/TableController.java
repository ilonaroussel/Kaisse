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

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.kaisse.model.Table;

public class TableController {

    @FXML
    private ListView<String> listTable;
    @FXML
    private GridPane form_popup;
    @FXML
    private TextField numberField;
    @FXML
    private TextField seatsField;
    @FXML
    private TextField emplacementField;

    @FXML
    protected void onTableButtonClick() {
        form_popup.setVisible(!form_popup.isVisible());
    }

    @FXML
    protected void getAllTablesFromDatabase() {
        try {
            MongoDatabase database = Main.database;
            MongoCollection<Document> collection = database.getCollection("Table");

            List<Document> documents = collection.find().into(new ArrayList<>());
            for (Document doc : documents) {
                System.out.println("Document : " + doc.toJson());
            }

            List<Table> tables = documents.stream()
                    .map(Table::createFromDocument)
                    .toList();

            ObservableList<String> tableDisplayList = FXCollections.observableArrayList();

            for (Table table : tables) {
                String display = "Table " + table.getNumber() +
                        " | Places : " + table.getSeats() +
                        " | Emplacement : " + table.getEmplacement() +
                        " | Libre : " + (table.getFree() ? "Disponible" : "Indisponible");
                System.out.println("Ajout au ListView : " + display);
                tableDisplayList.add(display);
            }

            listTable.setItems(tableDisplayList);
            System.out.println("Affichage terminé.");

        } catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        getAllTablesFromDatabase();
    }

    @FXML
    public void addTable() {
        try {
            Integer number = Integer.parseInt(numberField.getText());
            Integer seats = Integer.parseInt(seatsField.getText());
            String emplacement = emplacementField.getText();

            if (emplacement.isEmpty()) {
                System.out.println("Emplacement vide.");
                return;
            }

            MongoDatabase database = Main.database;
            MongoCollection<Document> collection = database.getCollection("Table");

            Document dish = new Document("number", number)
                    .append("seats", seats)
                    .append("emplacement", emplacement)
                    .append("isFree", true);

            collection.insertOne(dish);
            System.out.println("Table ajoutée.");

            numberField.clear();
            seatsField.clear();
            emplacementField.clear();

        } catch (NumberFormatException e) {
            System.out.println("Entrez des nombres valides pour le numéro et les places.");
        } catch (Exception e) {
            System.out.println("Erreur d'insertion : " + e.getMessage());
        }
    };
}
