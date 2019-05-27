package View;

import Model.MenuPanel;
import com.mysql.jdbc.exceptions.MySQLDataException;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import Model.NavigationButton;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class ViewManager {

    //Constants for managing layout
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    private final int HEIGHT = 600;
    private final int WIDTH = 800;
    private final int MENU_BUTTON_START_X = 100;
    private final int MENU_BUTTON_START_Y = 100;
    private final String TITLE = "View/resources/Title.png";

    private List<NavigationButton> menuButtons;
    private  MenuPanel helpPanel;
    private  MenuPanel creditsPanel;
    private static boolean twoPlayersMode = false;

    private MusicManager musicManager;

    public ViewManager() {
        //creating list to manage buttons
        menuButtons = new ArrayList<>();
        //initializing main components of window
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();
        mainStage.initStyle(StageStyle.UNDECORATED);
        mainStage.setTitle("Battle Metropolis");
        mainStage.setScene(mainScene);
        createButtons();
        createBackground();
        createHelpPanel();
        createCreditsPanel();
        createTitle();
        musicManager = new MusicManager();
        musicManager.playMenuTheme();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    ///////////////////////MENU METHODS//////////////////////////////
    private void createTitle() {
        ImageView title = new ImageView(TITLE);
        title.setFitWidth(450);
        title.setFitHeight(40);
        title.setLayoutX(WIDTH - 475);
        title.setLayoutY(50);
        mainPane.getChildren().add(title);
    }

    private void createButtons() {
        createPlayButton();
        createHelpButton();
        createCreditsButton();
        createExitButton();
    }

    private void addMenuButton(NavigationButton button) {
        button.setLayoutX(MENU_BUTTON_START_X);
        button.setLayoutY(MENU_BUTTON_START_Y + menuButtons.size()*100);
        //adding new button to button list
        menuButtons.add(button);
        //showing button on screen
        mainPane.getChildren().add(button);
    }

    private void createPlayButton() {
        NavigationButton playButton = new NavigationButton("PLAY");
        addMenuButton(playButton);

        playButton.setOnAction(event -> {
                MapChooseManager mapChooseManager = new MapChooseManager(mainStage,musicManager);
                mapChooseManager.createChooseMenu();
        });
    }

    private void createHelpButton() {
        NavigationButton helpButton = new NavigationButton("HELP");
        addMenuButton(helpButton);

        //action handler to call panel animation whenever button is pressed
        helpButton.setOnAction(event -> {
            musicManager.playClickSound();
            if (!creditsPanel.isHid())
                creditsPanel.movePanel();
            helpPanel.movePanel();
        });
    }

    private void createCreditsButton() {
        NavigationButton creditsButton = new NavigationButton("CREDITS");
        addMenuButton(creditsButton);

        creditsButton.setOnAction(event -> {
            musicManager.playClickSound();
            if (!helpPanel.isHid())
                helpPanel.movePanel();
            creditsPanel.movePanel();
        });
    }

    private void createExitButton() {
        NavigationButton exitButton = new NavigationButton("EXIT");
        addMenuButton(exitButton);

        //handler to exit app if button is pressed
        exitButton.setOnAction(event -> {
            musicManager.playClickSound();
            Platform.exit();
        });
    }

    private void createBackground() {
        Image backgroundMenuImage = new Image("View/resources/temp_background.png", WIDTH, HEIGHT, false, true);
        BackgroundImage backgroundMenu = new BackgroundImage(backgroundMenuImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
        mainPane.setBackground(new Background(backgroundMenu));
    }

    private void createHelpPanel() {
        helpPanel = new MenuPanel(800, 130);
        mainPane.getChildren().add(helpPanel);
    }

    private void createCreditsPanel() {
        creditsPanel = new MenuPanel(800, 130);
        mainPane.getChildren().add(creditsPanel);
    }

}
