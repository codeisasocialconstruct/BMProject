package View;
// TODO  damage system,

// TODO fix shooting delay (not variable, maybe timer object that count cooldown)

// TODO fix second player key listener (two listeners don`t work in the same time)

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

    //variables for animation
    private List<Tank> tanksList;
    private AnimationTimer gameTimer;

    private boolean gridMode;

    private final static int GAME_WIDTH = 800;  //Map divided into blocks 50x50 pixels each
    private final static int GAME_HEIGHT = 600; //Map has size 16x12 blocks
    private final static int BLOCK_SIZE = 50;
    private static String[][] positionMatrix;   //array used to detect collisions

    private final static String standardTankSprite = "Model/Resources/tankSprites/tank_dark.png";
    private final static String playerOneTankSprite = "Model/Resources/tankSprites/tank_red.png";
    private final static String playerTwoTankSprite = "Model/Resources/tankSprites/tankBlue.png";


    public GameViewManager() {
        initializeStage();
        createBackground();
    }

    private void initializeStage() {
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
        gameStage.initStyle(StageStyle.UNDECORATED); //hiding system window bar
        gameStage.setTitle("Battle Metropolis");
        gridMode = true;
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
        tanksList = new ArrayList<>();  //initializing array list that allows to manage all tanks on map
        positionMatrix = new String[GAME_WIDTH/BLOCK_SIZE][GAME_HEIGHT/BLOCK_SIZE]; //initializing new array to represent map
        this.menuStage = menuStage;
        this.menuStage.hide();

        createExitButton(); //adding exit button
        positionMatrix[0][0]="Exit";    //Blocking movement on exit button squares
        positionMatrix[1][0]="Exit";
        positionMatrix[2][0]="Exit";
        positionMatrix[3][0]="Exit";

        //spawning test tanks
        spawnPlayerTank(gamePane, gameScene, 8, 11, playerOneTankSprite, tanksList, positionMatrix,
                KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.DOWN, KeyCode.CONTROL);
        spawnNeutralTank(gamePane, 3, 2, standardTankSprite, tanksList, positionMatrix);
        spawnNeutralTank(gamePane, 8, 5, standardTankSprite, tanksList, positionMatrix);
        spawnNeutralTank(gamePane, 10, 5, standardTankSprite, tanksList, positionMatrix);
        createGameLoop();
        gameStage.show();
    }

    /*
    Timer to call animation in every frame
    After calling moveTank, moveIterator is set to BlockSize/5 that in this case equals 9.
    Now continueTankMovement will be called instead of moveTank.
    This makes sure, user input won`t interrupt animation, because continueTankMovement does not depends on user input.
    Thanks to that, we make sure that tank is moving only by 50, which is map block size and animation will be smooth.
    */
    private void createGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for(Tank tanks: tanksList) {
                    tanks.moveTank();   //moving every tank on the map every frame
                    tanks.moveProjectiles();
                }
            }
        };
        gameTimer.start();
    }

    //function that checks if spawn position is empty and coordinates are correct, if they are, the Tank constructor is called
    private boolean spawnNeutralTank(AnchorPane gamePane, int spawnPosArrayX, int spawnPosArrayY,
                                     String tankSpriteUrl, List<Tank> tankList, String[][] collisionMatrix) {
        if (collisionMatrix[spawnPosArrayX][spawnPosArrayY]==null && spawnPosArrayX<GAME_WIDTH/BLOCK_SIZE && spawnPosArrayY<GAME_HEIGHT/BLOCK_SIZE) {
            Tank spawningTank = new Tank(gamePane, spawnPosArrayX , spawnPosArrayY, tankSpriteUrl, tankList, collisionMatrix);
            return true;
        }
        else
            return false;
    }

    //function that checks if spawn position is empty and coordinates are correct, if they are, the PlayerTank constructor is called
    private boolean spawnPlayerTank(AnchorPane gamePane, Scene gameScene, int spawnPosArrayX, int spawnPosArrayY, String tankSpriteUrl, List<Tank> tankList, String[][] collisionMatrix,
                                    KeyCode moveLeftKey, KeyCode moveRightKey, KeyCode moveUpKey, KeyCode moveDownKey, KeyCode shootKey) {
        if (collisionMatrix[spawnPosArrayX][spawnPosArrayY]==null && spawnPosArrayX<GAME_WIDTH/BLOCK_SIZE && spawnPosArrayY<GAME_HEIGHT/BLOCK_SIZE) {
            TankPlayer spawningTank = new TankPlayer(gamePane, gameScene, spawnPosArrayX , spawnPosArrayY, tankSpriteUrl, tankList, collisionMatrix,
                    moveLeftKey, moveRightKey, moveUpKey, moveDownKey, shootKey);
            return true;
        }
        else
            return false;
    }


}
