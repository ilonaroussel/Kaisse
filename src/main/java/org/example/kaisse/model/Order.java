package org.example.kaisse.model;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.example.kaisse.Main;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Order {
    private final ObjectId id;
    private String state;
    private Table table;
    private LocalDateTime date;
    private ArrayList<Dish> dishes;

    public Order(ObjectId id, String state, Table table, LocalDateTime date, ArrayList<Dish> dishes) {
        this.id = id;
        this.state = state;
        this.table = table;
        this.date = date;
        this.dishes = dishes;
    }

    public static Order createFromDocument(Document doc) {
        return new Order(
                doc.getObjectId("_id"),
                doc.getString("state"),
                Table.createFromDocument(doc.getList("table", Document.class).getFirst()),
                doc.getDate("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                doc.getList("dishes", Document.class)
                        .stream()
                        .map(Dish::createFromDocument)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    public Document convertToDocument() {
        return new Document("_id", this.id)
                .append("state", this.state)
                .append("table", this.table.getId())
                .append("date", this.date)
                .append("dishes", this.dishes
                        .stream()
                        .map(Dish::getId)
                        .toList());
    }

    public void insertToDb() {
        MongoCollection<Document> collection = Main.database.getCollection("Order");

        Document doc = this.convertToDocument();

        try {
            collection.insertOne(doc);
            System.out.println("Inserted: " + this);
        } catch (Exception e) {
            System.out.println("Fail Insert: " + e);
        }
    }

    public void deleteFromDb() {
        MongoCollection<Document> collection = Main.database.getCollection("Order");

        Bson filter = Filters.eq("_id", this.id);

        try {
            collection.deleteOne(filter);
            System.out.println("Deleted: " + this);
        } catch (Exception e) {
            System.out.println("Fail Delete: " + e);
        }
    }

    public void validate() {
        MongoCollection<Document> collection = Main.database.getCollection("Order");

        this.state = "VALIDATED";
        Bson filter = Filters.eq("_id", this.id);
        Bson update = Updates.set("state", this.state);

        try {
            collection.updateOne(filter, update);
            System.out.println("Updated: " + this);
        } catch (Exception e) {
            System.out.println("Fail Update: " + e);
        }
    }

    public ObjectId getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
    }
}
