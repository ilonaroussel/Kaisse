package org.example.kaisse.model;

import org.bson.Document;

public class Ingredient {
    private String name;
    private Integer quantity;
    private Double price ;

    public Ingredient(String name, Integer quantity, Double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public static Ingredient createFromDocument(Document doc) {
        return new Ingredient(
                doc.getString("name"),
                doc.getInteger("quantity"),
                doc.getDouble("price")
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
