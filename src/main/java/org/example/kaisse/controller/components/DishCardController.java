package org.example.kaisse.controller.components;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.kaisse.model.Dish;

public class DishCardController {

    @FXML
    private Label dishName;
    @FXML private Label dishPrice;
    @FXML private Label dishDescription;
    @FXML private ImageView dishImage;
    @FXML private VBox dishIngredientList;

    public void setDish(Dish dish) {
        dishName.setText(dish.getName());
        dishPrice.setText(String.format("%.2f€", dish.getPrice()));
        dishDescription.setText(dish.getDescription());
        dishImage.setImage(new Image(dish.getImage()));

        dishIngredientList.getChildren().clear();
        if (dish.getIngredients() != null) {
            dish.getIngredients().forEach(ingredient -> {
                System.out.println("coucou");
                Label label = new Label("- " + ingredient.getName() + " (" + ingredient.getQuantity() + "g)");
                dishIngredientList.getChildren().add(label);
            });
        }
    }
}

