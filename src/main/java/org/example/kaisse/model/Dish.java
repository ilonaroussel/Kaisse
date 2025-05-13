package org.example.kaisse.model;

import org.bson.Document;

public class Dish {
    private String name;
    private String description;
    private Float price;
    private String image;

    public Dish(String image, Float price, String description, String name) {
        this.image = image;
        this.price = price;
        this.description = description;
        this.name = name;
    }

    public static Dish createFromDocument(Document doc) {
        return new Dish(
                (String) doc.get("image"),
                Float.parseFloat(doc.get("price").toString()),
                (String) doc.get("description"),
                (String) doc.get("name")
        );
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
