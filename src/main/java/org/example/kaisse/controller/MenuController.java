package org.example.kaisse.controller;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.kaisse.Main;
import org.example.kaisse.controller.components.DishCardController;
import org.example.kaisse.model.Dish;
import org.example.kaisse.model.Ingredient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MenuController {
    @FXML private GridPane form_ingredient;
    @FXML private TextArea ingredientQuantityField1;
    @FXML private TextField ingredientPriceField1;
    @FXML private TextField ingredientNameField1;
    @FXML private GridPane form_popup;
    @FXML private ListView<HBox> dishList;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField imageField;
    private Ingredient ingredient;
    private ObservableList<HBox> observableDishes;
    private ArrayList< Ingredient> ingredients = new ArrayList<>();



    @FXML
    protected void onDishButtonClick() {
        Dialog<Void> formDialog = new Dialog<>();
        DialogPane dialogPane = formDialog.getDialogPane();
        formDialog.setHeight(500);
        formDialog.setWidth(500);
        dialogPane.setContent(form_popup);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        form_popup.setVisible(true);
        formDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String name = nameField.getText();
                String description = descriptionField.getText();
                double price = Float.parseFloat(priceField.getText());
                String image = imageField.getText();

                addDish(name, description, price, image, ingredients);
            }
            return null;
        });
        formDialog.showAndWait();
        form_popup.setVisible(false);
    }

    @FXML
    public void addDish(String name, String description, Double price, String image, ArrayList<Ingredient> ingredientList) {

        MongoDatabase database = Main.database;
            MongoCollection<Document> collection = database.getCollection("Dish");

            Dish finalDish = new Dish(ObjectId.get(), name,description,price,image, ingredientList);

            nameField.clear();
            descriptionField.clear();
            priceField.clear();
            imageField.clear();

        try {
            collection.insertOne(finalDish.convertToDocument());
            observableDishes.setAll(getAllDishes(database));
        } catch (Exception e) {
            System.out.println("Fail Insert: " + e);
        }
    };

    @FXML
    protected void initialize(){
        MongoDatabase database = Main.database;
        observableDishes = FXCollections.observableArrayList();
        dishList.setItems(observableDishes);
        observableDishes.setAll(getAllDishes(database));

    }

    protected ObservableList<HBox> getAllDishes(MongoDatabase database){
        MongoCollection<Document> collection = database.getCollection("Dish");

        List<Document> documents = collection.find().into(new ArrayList<>());
        List<Dish> dishes = documents.stream().map(Dish::createFromDocument).toList();

        return FXCollections.observableArrayList(
                dishes.stream().map(dish -> {
                    HBox dishCard = new HBox(20);
                    dishCard.setMinHeight(50);
                    dishCard.setMinWidth(75);

                    Image image = new Image(dish.getImage());
                    ImageView dishImage = new ImageView();
                    dishImage.setFitHeight(50);
                    dishImage.setFitWidth(75);
                    dishImage.setImage(image);

                    Label dishName = new Label(dish.getName());
                    Label dishPrice = new Label(String.format("%.2f€", dish.getPrice()));
                    Label dishDescription = new Label(dish.getDescription());


                    dishCard.getChildren().addAll(dishImage, dishName, dishPrice, dishDescription);

                        dishCard.setOnMouseClicked(_ -> {
                            try {
                                displayDishCard(dish);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });


                    return dishCard;
                }).toList());
    }

    protected void displayDishCard(Dish displayedDish) throws IOException {
        Dialog<Void> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialog.setHeight(500);
        dialog.setWidth(750);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/kaisse/components/dish-card.fxml"));
        AnchorPane cardContent = loader.load();
        DishCardController controller = loader.getController();
        controller.setDish(displayedDish);

        dialogPane.setContent(cardContent);
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    protected void createIngredient() {

        Dialog<Void> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        dialog.setHeight(500);
        dialog.setWidth(700);

        dialogPane.setContent(form_ingredient);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        form_ingredient.setVisible(true);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String name = ingredientNameField1.getText();
                Integer quantity = Integer.parseInt(ingredientQuantityField1.getText());
                double price = Double.parseDouble(ingredientPriceField1.getText());
                ingredient = new Ingredient(name,quantity,price);
            }
            return null;
        });
        dialog.showAndWait();
        form_ingredient.setVisible(false);
        ingredients.add(ingredient);
        System.out.println(ingredients);

        ingredientNameField1.clear();
        ingredientQuantityField1.clear();
        ingredientPriceField1.clear();

    }
}
