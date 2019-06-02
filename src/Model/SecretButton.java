package Model;

import javafx.scene.control.Button;

public class SecretButton extends Button {

    private final String BUTTON_STYLE = "-fx-background-color: transparent";

    public SecretButton() {
        setPrefHeight(48);
        setPrefWidth(200);
        setStyle(BUTTON_STYLE);
    }
}
