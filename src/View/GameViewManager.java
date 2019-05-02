package View;
// TODO create Tank class and PlayerTank class, move methods there

// TODO  collision system,

// TODO  shooting system,

// TODO  enemies

import Model.NavigationButton;
import Model.Tanks.Tank;
import Model.Tanks.TankPlayer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;


public class GameViewManager {
    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private Stage menuStage;
    private Tank testTank;
    private Tank playerOneTank;

    //variables for animation
    private List<Tank> tanksList;
    private AnimationTimer gameTimer;

    private boolean gridMode;

    private final static int GAME_WIDTH = 800;  //Map divided into blocks 50x50 pixels each
    private final static int GAME_HEIGHT = 600; //Map has size 16x12 blocks
    private final static int BLOCK_SIZE = 50;
    private final static String standardTankSprite = "Model/Resources/tankSprites/tank_dark.png";
    private final static String playerOneTankSprite = "Model/Resources/tankSprites/tank_red.png";


    public GameViewManager() {
        initializeStage();
        createBackground();
    }

    private void initializeStage() {
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
        gameStage.initStyle(StageStyle.UNDECORATED);
        gameStage.setTitle("Battle Metropolis");
        gridMode = false;
    }

    private void createBackground() {
        Image backgroundGameImage;
        if (!gridMode) {
           backgroundGameImage = new Image("View/resources/texture.png", BLOCK_SIZE, BLOCK_SIZE, true, true);
        }
        else {
            backgroundGameImage = new Image("View/resources/texturegrid.png", BLOCK_SIZE, BLOCK_SIZE, true, true);
        }

        BackgroundImage backgroundGame = new BackgroundImage(backgroundGameImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
        gamePane.setBackground(new Background(backgroundGame));
    }

    //Creating Listeners to inform which buttons are pressed - used to determine which animation is called


    private void createExitButton() {
        NavigationButton exitButton = new NavigationButton("EXIT");
        exitButton.setLayoutX(0);
        exitButton.setLayoutY(0);
        //showing button on screen
        gamePane.getChildren().add(exitButton);

        //handler to exit app if button is pressed
        exitButton.setOnAction(event -> Platform.exit());
    }

    //showing game window
    public void createGame(Stage menuStage, boolean twoPlayersMode) {
        tanksList = new ArrayList<>();
        this.menuStage = menuStage;
        this.menuStage.hide();
        createExitButton();
        testTank = new Tank(gamePane, GAME_WIDTH/2, GAME_HEIGHT/2, standardTankSprite, tanksList);
        testTank = new Tank(gamePane, GAME_WIDTH/2, GAME_HEIGHT/2, standardTankSprite, tanksList);
        testTank = new Tank(gamePane, GAME_WIDTH/2, GAME_HEIGHT/2, standardTankSprite, tanksList);
        playerOneTank = new TankPlayer(gamePane, gameScene, GAME_WIDTH/2, GAME_HEIGHT-50, playerOneTankSprite, tanksList,
                KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.DOWN, KeyCode.SPACE);
        createGameLoop();
        gameStage.show();
    }

    /*
    Timer to call animation in every frame
    After calling moveTank, moveIterator is set to 9, so continueTankMovement will be called instead of moveTank.
    That make sure, user input won`t interrupt animation, because continueTankMovement does not depends on user input.
    Thanks to that, we make sure that tank is moving only by 50 pixels, which is map block size and animation will be smooth.
    */
    private void createGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for(Tank tanks: tanksList)
                    tanks.moveTank();
            }
        };
        gameTimer.start();
    }




}
