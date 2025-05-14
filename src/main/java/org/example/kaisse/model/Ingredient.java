package org.example.kaisse.model;

import org.bson.Document;

public class Ingredient {
    private String name;
    private Integer quantity;
    private double price ;

    public Ingredient(String name, Integer quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
    public Document transformIngredientsIntoDocument() {
        return new Document()
                .append("name", name)
                .append("quantity", quantity)
                .append("price", price);
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
