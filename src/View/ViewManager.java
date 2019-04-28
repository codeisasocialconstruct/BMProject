package View;

import Model.InfoLabel;
import Model.MenuPanel;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Model.NavigationButton;

import java.util.ArrayList;
import java.util.List;

public class ViewManager {

    //Constants for managing layout
    private final int HEIGHT = 600;
    private final int WIDTH = 800;
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;
    private final int MENU_BUTTON_START_X = 100;
    private final int MENU_BUTTON_START_Y = 100;

    private List<NavigationButton> menuButtons;
    private  MenuPanel helpPanel;

    public ViewManager() {
        menuButtons = new ArrayList<>(); //creating list to manage buttons
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();

        mainStage.setScene(mainScene);
        createButtons();
        CreateBackground();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private void createButtons() {
        createPlayButton();
        createHelpButton();
        createOptionsButton();
        createExitButton();
    }

    private void addMenuButton(NavigationButton button) {
        button.setLayoutX(MENU_BUTTON_START_X);
        button.setLayoutY(MENU_BUTTON_START_Y + menuButtons.size()*100);
        menuButtons.add(button); //adding new button to button list
        mainPane.getChildren().add(button); //showing button on screen
    }

    private void createPlayButton() {
        NavigationButton playButton = new NavigationButton("PLAY");
        addMenuButton(playButton);
    }

    private void createHelpButton() {
        NavigationButton helpButton = new NavigationButton("HELP");
        addMenuButton(helpButton);

        helpButton.setOnAction(event -> helpPanel.movePanel()); //action handler to call panel animation whenever button is pressed
    }

    private void createOptionsButton() {
        NavigationButton optionsButton = new NavigationButton("OPTIONS");
        addMenuButton(optionsButton);
    }

    private void createExitButton() {
        NavigationButton exitButton = new NavigationButton("EXIT");
        addMenuButton(exitButton);

        exitButton.setOnAction(event -> Platform.exit()); //handler to exit app if button is pressed
    }

    private void CreateBackground() {
        Image backgroundMenuImage = new Image("View/resources/temp_background.png", WIDTH, HEIGHT, false, true);
        BackgroundImage backgroundMenu = new BackgroundImage(backgroundMenuImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
        mainPane.setBackground(new Background(backgroundMenu));
    }

    private void CreateHelpPanel() {
        helpPanel = new MenuPanel();
        mainPane.getChildren().add(helpPanel);
    }
}
