package View;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Model.NavigationButton;

import java.util.ArrayList;
import java.util.List;

public class ViewManager {

    private final int HEIGHT = 600;
    private final int WIDTH = 800;
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;
    private final int MENU_BUTTON_START_X = 100;
    private final int MENU_BUTTON_START_Y = 100;

    private List<NavigationButton> menuButtons;

    public ViewManager() {
        menuButtons = new ArrayList<>();
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();

        mainStage.setScene(mainScene);
        CreateButtons(new NavigationButton("PLAY"));
        CreateButtons(new NavigationButton("HELP"));
        CreateButtons(new NavigationButton("OPTIONS"));
        CreateButtons(new NavigationButton("EXIT"));
        CreateBackground();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private void CreateButtons(NavigationButton button) {
        button.setLayoutX(MENU_BUTTON_START_X);
        button.setLayoutY(MENU_BUTTON_START_Y + menuButtons.size()*100);
        menuButtons.add(button);
        mainPane.getChildren().add(button);
    }

    private void CreateBackground() {
        Image backgroundMenuImage = new Image("View/resources/temp_background.png", WIDTH, HEIGHT, false, true);
        BackgroundImage backgroundMenu = new BackgroundImage(backgroundMenuImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
        mainPane.setBackground(new Background(backgroundMenu));
    }
}
