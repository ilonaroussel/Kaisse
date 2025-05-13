package org.example.kaisse.model;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Dish {
    private final ObjectId id;
    private String name;
    private String description;
    private Float price;
    private String image;

    public Dish(ObjectId id, String name, String description, Float price, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public static Dish createFromDocument(Document doc) {
        return new Dish(
                doc.getObjectId("_id"),
                doc.getString("name"),
                doc.getString("description"),
                doc.get("price", Float.class),
                doc.getString("image")
        );
    }

    public ObjectId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
