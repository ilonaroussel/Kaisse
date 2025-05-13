package org.example.kaisse.model;

import org.bson.Document;

public class Dish {
    private String name;
    private String description;
    private Float price;
    private String image;

    public Dish(String name, String description, Float price, String image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public static Dish createFromDocument(Document doc) {
        return new Dish(
                doc.get("name").toString(),
                doc.get("description").toString(),
                Float.parseFloat(doc.get("price").toString()),
                doc.get("image").toString()
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
