package View;

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

    private final static int GAME_WIDTH = 250;  //Map divided into blocks 50x50 pixels each
    private final static int GAME_HEIGHT = 250; //Map has size 16x12 blocks
    private final static int BLOCK_SIZE = 50;
    private static String[][] positionMatrix;
    private MapManager mapManager;
    //array used to detect collisions. It contains strings. If string is a number
    //that means in this position tank is present and number equals it`s. If any other string

    private final static String standardTankSprite = "Model/Resources/tankSprites/tank_dark.png";
    private final static String playerOneTankSprite = "Model/Resources/tankSprites/tank_red.png";
    private final static String playerTwoTankSprite = "Model/Resources/tankSprites/tankBlue.png";

    ///////////////////////WINDOW INITIALIZATION////////////////////////////////////
    public GameViewManager() {
        initializeStage();
    }

    private void initializeStage() {
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
        gameStage.initStyle(StageStyle.UNDECORATED); //hiding system window bar
        gameStage.setTitle("Battle Metropolis");
        gridMode = false; //creting grid
        mapManager = new MapManager(gamePane,gameScene,gameStage);
    }

    private void createBackground() //TODO object MapManager, map generating and background generating
    {
        mapManager.createBackground();
        positionMatrix = mapManager.createPositionMatrix(); //initializing new array to represent map
        mapManager.createMap();

    }

    private void createExitButton() {
        NavigationButton exitButton = new NavigationButton("EXIT");
        exitButton.setLayoutX(0);
        exitButton.setLayoutY(0);
        //showing button on screen
        gamePane.getChildren().add(exitButton);

        //handler to exit app if button is pressed
        exitButton.setOnAction(event -> Platform.exit());
    }

    //////////////////////////GAME ELEMENTS////////////////////////////////////////
    //showing game window
    public void createGame(Stage menuStage, boolean twoPlayersMode) {
        tanksList = new ArrayList<>();  //initializing array list that allows to manage all tanks on map
        createBackground();
        this.menuStage = menuStage;
        this.menuStage.hide();


        //spawning test tanks
        spawnPlayerTank(gamePane, gameScene, 2, 4, playerOneTankSprite, tanksList, positionMatrix,
                KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.DOWN, KeyCode.CONTROL);
        //spawnNeutralTank(gamePane, 3, 2, standardTankSprite, tanksList, positionMatrix);
        //spawnNeutralTank(gamePane, 8, 5, standardTankSprite, tanksList, positionMatrix);
        //spawnNeutralTank(gamePane, 10, 5, standardTankSprite, tanksList, positionMatrix);

        mapManager.bushToFront();
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
                for(int iterator = 0; iterator<tanksList.size(); iterator++) {
                    if(tanksList.get(iterator).getLifePoints()>0) {
                        tanksList.get(iterator).moveTank();   //moving every tank on the map every frame
                        tanksList.get(iterator).moveProjectiles();
                    }
                    else {
                        tanksList.get(iterator).tankDestruction();
                        tanksList.remove(iterator);
                    }
                }
            }
        };
        gameTimer.start();
    }

    /////////////////////////////////SPAWN METHODS////////////////////////////////////////////////////////////

    //function that checks if spawn position is empty and coordinates are correct, if they are, the Tank constructor is called
    private boolean spawnNeutralTank(AnchorPane gamePane, int spawnPosArrayX, int spawnPosArrayY,
                                     String tankSpriteUrl, List<Tank> tankList, String[][] collisionMatrix) {
        if (collisionMatrix[spawnPosArrayX][spawnPosArrayY]==null && spawnPosArrayX<GAME_WIDTH/BLOCK_SIZE && spawnPosArrayY<GAME_HEIGHT/BLOCK_SIZE) {
            Tank spawningTank = new Tank(gamePane, spawnPosArrayX , spawnPosArrayY, tankSpriteUrl, tankList, collisionMatrix, 3);
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
