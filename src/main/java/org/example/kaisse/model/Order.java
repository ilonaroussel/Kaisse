package org.example.kaisse.model;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.example.kaisse.Main;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class Order {
    private final ObjectId id;
    private String state;
    private Table table;
    private LocalDateTime date;
    private ArrayList<OrderDish> dishes;

    public Order(ObjectId id, String state, Table table, LocalDateTime date, ArrayList<OrderDish> dishes) {
        this.id = id;
        this.state = state;
        this.table = table;
        this.date = date;
        this.dishes = dishes;
    }

    // Static function to create an order from a document
    public static Order createFromDocument(Document doc) {
        MongoCollection<Document> tableCollection = Main.database.getCollection("Table");

        return new Order(
                doc.getObjectId("_id"),
                doc.getString("state"),
                Table.createFromDocument(Objects.requireNonNull(tableCollection
                        .find(eq("_id", doc.getObjectId("table")))
                        .first())),
                doc.getDate("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                doc.getList("dishes", Document.class)
                        .stream()
                        .map(OrderDish::createFromDocument)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    // Converts the Order to a Document
    public Document convertToDocument() {
        return new Document("_id", this.id)
                .append("state", this.state)
                .append("table", this.table.getId())
                .append("date", this.date)
                .append("dishes", this.dishes
                        .stream()
                        .map(OrderDish::convertToDocument)
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

    public void addDish(Dish dish) {
        MongoCollection<Document> collection = Main.database.getCollection("Order");

        // Check if the dish already exists in the order
        boolean found = false;
        for (OrderDish orderDish : this.dishes) {
            if (orderDish.getDish().getId().equals(dish.getId())) {
                orderDish.setQuantity(orderDish.getQuantity() + 1); // Increment quantity
                found = true;

                break;
            }
        }

        if (!found) {
            this.dishes.add(new OrderDish(1, dish)); // Add new dish
        }

        // Update in database: store list of documents with dish ID and quantity
        Bson filter = eq("_id", this.id);
        Bson update = Updates.set("dishes", this.dishes.stream()
                .map(OrderDish::convertToDocument)
                .toList());

        try {
            collection.updateOne(filter, update);
            System.out.println("Updated: " + this);
        } catch (Exception e) {
            System.out.println("Fail Update: " + e);
        }
    }

    public void removeDish(OrderDish orderDish) {
        MongoCollection<Document> collection = Main.database.getCollection("Order");

        this.dishes.remove(orderDish);

        Bson filter = eq("_id", this.id);
        Bson update = Updates.set("dishes", this.dishes
                .stream()
                .map(OrderDish::convertToDocument)
                .toList());

        try {
            collection.updateOne(filter, update);
            System.out.println("Updated: " + this);
        } catch (Exception e) {
            System.out.println("Fail Update: " + e);
        }
    }

    public void changeState(String state) {
        MongoCollection<Document> collection = Main.database.getCollection("Order");

        this.state = state;
        Bson filter = eq("_id", this.id);
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

    public ArrayList<OrderDish> getDishes() {
        return dishes;
    }

    public void setDishes(ArrayList<OrderDish> dishes) {
        this.dishes = dishes;
    }
}
