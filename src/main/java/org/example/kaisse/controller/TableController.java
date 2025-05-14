package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
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
    protected void getAllTablesFromDatabase() {
        System.out.println("👉 Méthode getAllTablesFromDatabase() appelée");

        try {
            MongoDatabase database = Main.database;
            if (database == null) {
                System.out.println("❌ ERREUR : Base de données non initialisée !");
                return;
            }

            MongoCollection<Document> collection = database.getCollection("Table");
            if (collection == null) {
                System.out.println("❌ ERREUR : Collection 'Table' introuvable !");
                return;
            }

            List<Document> documents = collection.find().into(new ArrayList<>());
            System.out.println("✅ Nombre de documents trouvés : " + documents.size());

            for (Document doc : documents) {
                System.out.println("📄 Document : " + doc.toJson());
            }

            List<Table> tables = documents.stream()
                    .map(Table::createFromDocument)
                    .toList();

            ObservableList<String> tableDisplayList = FXCollections.observableArrayList();

            for (Table table : tables) {
                String display = "Table " + table.getNumber() +
                        " | Places: " + table.getSeats() +
                        " | Emplacement: " + table.getEmplacement() +
                        " | Libre: " + (table.getFree() ? "true" : "false");

                System.out.println("🟢 Ajout au ListView : " + display);
                tableDisplayList.add(display);
            }

            listTable.setItems(tableDisplayList);
            System.out.println("✅ Affichage terminé.");

        } catch (Exception e) {
            System.out.println("❌ Exception : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        getAllTablesFromDatabase();
    }
}
