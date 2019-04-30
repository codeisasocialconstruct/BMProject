package Model;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class InfoLabel extends Label {
    private static final String FONT_PATH = "src/Model/Resources/Fonts/HeartbitXX.ttf";

    public InfoLabel (String text) {
        setPrefWidth(400);
        setPrefHeight(250);
        setPadding(new Insets(40,40,40,40));
        setText(text);
        setWrapText(true);
        setTextFill(Color.WHITE);  //changing font color
        setLabelFont();
    }

    private void setLabelFont() {
        try {
            setFont(Font.loadFont(new FileInputStream(new File(FONT_PATH)), 25));
        } catch (FileNotFoundException e) {
            setFont(Font.font("Comic Sans", 17)); //if font is missing loading comic sans
        }
    }
}
