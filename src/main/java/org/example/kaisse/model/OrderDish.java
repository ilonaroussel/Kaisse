package org.example.kaisse.model;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.example.kaisse.Main;

import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class OrderDish {
    Integer quantity;
    Dish dish;

    public OrderDish(Integer quantity, Dish dish) {
        this.quantity = quantity;
        this.dish = dish;
    }

    public static OrderDish createFromDocument(Document doc) {
        MongoCollection<Document> dishCollection = Main.database.getCollection("Dish");

        return new OrderDish(
                doc.getInteger("quantity"),
                Dish.createFromDocument(Objects.requireNonNull(dishCollection
                        .find(eq("_id", doc.getObjectId("dish")))
                        .first()))
        );
    }

    public Document convertToDocument() {
        return new Document("quantity", this.quantity)
                .append("dish", this.dish.getId());
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }
}
