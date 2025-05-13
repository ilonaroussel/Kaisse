package org.example.kaisse.controller;

import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;

public class MenuController {
    @FXML
    public GridPane form_popup;

    @FXML
    protected void onDishButtonClick(){
        form_popup.setVisible(!form_popup.isVisible());
    };

}
