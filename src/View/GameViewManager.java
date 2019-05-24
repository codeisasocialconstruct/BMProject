package View;

// TODO fix second player key listener (two listeners don`t work in the same time)

import Model.InfoLabel;
import Model.MapElements.Base;
import Model.MapElements.BrickBlock;
import Model.MenuPanel;
import Model.NavigationButton;
import Model.Tanks.Tank;
import Model.Tanks.TankPlayer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class GameViewManager
{
    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private Stage menuStage;

    //variables for animation
    private List<Tank> tanksList;
    private Tank playerOneTank;
    private AnimationTimer gameTimer;
    private Base base;
    private DataBaseConnector dataBaseConnector;
    private String boardName = "Desert skirmish";
    private MapManager mapManager;

    private boolean isGamePaused;

    private boolean gridMode;

    private static int GAME_WIDTH;  //Map divided into blocks 50x50 pixels each
    private static int GAME_HEIGHT; //Map has size 16x12 blocks
    private static int BLOCK_SIZE;
    private static String[][] positionMatrix;
    private ArrayList<BrickBlock> brickList;
    //array used to detect collisions. It contains strings. If string is a number
    //that means in this position tank is present and number equals it`s. If any other string

    private final static String standardTankSprite = "Model/Resources/tankSprites/tank_dark.png";
    private final static String playerOneTankSprite = "Model/Resources/tankSprites/tank_red.png";
    private final static String playerTwoTankSprite = "Model/Resources/tankSprites/tankBlue.png";

    private MusicManager musicManager;

    ///////////////////////WINDOW INITIALIZATION////////////////////////////////////
    public GameViewManager(MusicManager musicManager)
    {
        this.musicManager = musicManager;
        initializeStage();
        createBackground();
        musicManager.playMainTheme();
    }

    private void initializeStage()
    {
        dataBaseConnector = new DataBaseConnector("SELECT * FROM map WHERE name = '" + boardName + "';");
        dataBaseConnector.getData();
        GAME_WIDTH = dataBaseConnector.getGame_width();
        GAME_HEIGHT = dataBaseConnector.getGame_height();
        BLOCK_SIZE = 50;
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
        gameStage.initStyle(StageStyle.UNDECORATED); //hiding system window bar
        gameStage.setTitle("Battle Metropolis");
        gridMode = false;
        isGamePaused = false;
        mapManager = new MapManager(gamePane, gameScene, gameStage, dataBaseConnector);
    }

    private void createBackground()
    {
        mapManager.createBackground();
        positionMatrix = mapManager.createPositionMatrix();
        brickList = mapManager.getBrickList();
    }


    private void createExitButton(double X, double Y)
    {
        NavigationButton exitButton = new NavigationButton("EXIT");
        exitButton.setLayoutX(X);
        exitButton.setLayoutY(Y);
        //showing button on screen
        gamePane.getChildren().add(exitButton);

        //handler to exit app if button is pressed
        exitButton.setOnAction(event ->
        {
            musicManager.playClickSound();
            Platform.exit();
        });
    }

    //////////////////////////GAME ELEMENTS////////////////////////////////////////
    //showing game window
    public void createGame(Stage menuStage, boolean twoPlayersMode)
    {
        tanksList = new ArrayList<>();  //initializing array list that allows to manage all tanks on map
        this.menuStage = menuStage;
        this.menuStage.hide();

        spawnBase(gamePane, mapManager.getBaseX(), mapManager.getBaseY(), positionMatrix, 5); //BASE NEED TO BE INITIALIZED BEFORE TANKS!!!
        spawnPlayerOneTank(gamePane, gameScene, mapManager.getPlayerOneX(), mapManager.getPlayerOneY(), playerOneTankSprite, tanksList, positionMatrix,
                KeyCode.LEFT, KeyCode.RIGHT, KeyCode.UP, KeyCode.DOWN, KeyCode.CONTROL, dataBaseConnector,  brickList);

        for(Point i : mapManager.getNeutralList())
        {
            spawnNeutralTank(gamePane, (int)i.getX(), (int)i.getY(), standardTankSprite, tanksList, positionMatrix,dataBaseConnector, brickList);
        }
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
    private void createGameLoop()
    {
        gameTimer = new AnimationTimer()
        {

            @Override
            public void handle(long now)
            {
                if (!isGamePaused)
                {
                    for (int iterator = 0; iterator < tanksList.size(); iterator++)
                    {
                        if (tanksList.get(iterator).getLifePoints() > 0)
                        { //checking if tank is alive
                            tanksList.get(iterator).moveTank();   //moving every tank on the map every frame
                            tanksList.get(iterator).moveProjectiles();
                        } else
                        {
                            tanksList.get(iterator).tankDestruction();
                            tanksList.remove(iterator);
                        }
                    }
                    if (playerOneTank.getLifePoints() == 0 || base.getLifePoints() == 0)
                    {
                        showLoseScreen();
                    }
                    if (mapManager.getNeutralCounter())
                    {
                        if (tanksList.size() == 1)
                        {
                            if (tanksList.get(0) instanceof TankPlayer)
                                showWinScreen();
                        }
                    }
                    mapManager.bushToFront();
                }
            }
        };
        gameTimer.start();
    }

    private void createShadowOverlay()
    {
        Rectangle overlay = new Rectangle(0, 0, GAME_WIDTH, GAME_HEIGHT);
        overlay.setOpacity(0.7);
        gamePane.getChildren().add(overlay);
    }

    private void createGamePanel()
    {
        MenuPanel winPanel = new MenuPanel(GAME_WIDTH / 2 - 200, GAME_HEIGHT / 2 - 150);
        gamePane.getChildren().add(winPanel);
        createExitButton(GAME_WIDTH / 2 - 99, GAME_HEIGHT / 2 + 70);
    }

    /////////////////////////////////SPAWN METHODS////////////////////////////////////////////////////////////

    //function that checks if spawn position is empty and coordinates are correct, if they are, the Tank constructor is called
    private boolean spawnNeutralTank(AnchorPane gamePane, int spawnPosArrayX, int spawnPosArrayY,
                                     String tankSpriteUrl, List<Tank> tankList, String[][] collisionMatrix, DataBaseConnector dataBaseConnector, ArrayList<BrickBlock> brickList)
    {
        if (spawnPosArrayX < GAME_WIDTH / BLOCK_SIZE && spawnPosArrayY < GAME_HEIGHT / BLOCK_SIZE)
        {
            if (collisionMatrix[spawnPosArrayX][spawnPosArrayY] == null)
            {
                Tank spawningTank = new Tank(gamePane, spawnPosArrayX, spawnPosArrayY, tankSpriteUrl,
                        tankList, collisionMatrix, 3, base, dataBaseConnector, brickList);
                return true;
            }
        }
        return false;
    }

    //function that checks if spawn position is empty and coordinates are correct, if they are, the PlayerTank constructor is called
    private boolean spawnPlayerOneTank(AnchorPane gamePane, Scene gameScene, int spawnPosArrayX, int spawnPosArrayY, String tankSpriteUrl, List<Tank> tankList, String[][] collisionMatrix,
                                       KeyCode moveLeftKey, KeyCode moveRightKey, KeyCode moveUpKey, KeyCode moveDownKey, KeyCode shootKey, DataBaseConnector dataBaseConnector,
                                       ArrayList<BrickBlock> brickList)
    {
        if (spawnPosArrayX < GAME_WIDTH / BLOCK_SIZE && spawnPosArrayY < GAME_HEIGHT / BLOCK_SIZE)
        {
            if (collisionMatrix[spawnPosArrayX][spawnPosArrayY] == null)
            {
                playerOneTank = new TankPlayer(gamePane, gameScene, spawnPosArrayX, spawnPosArrayY,
                        tankSpriteUrl, tankList, collisionMatrix, base,
                        moveLeftKey, moveRightKey, moveUpKey, moveDownKey, shootKey, dataBaseConnector,brickList);
                return true;
            }
        }
        return false;
    }

    private boolean spawnBase(AnchorPane gamePane, int spawnPosArrayX, int spawnPosArrayY, String[][] collisionMatrix, int lifePoints)
    {
        //Making sure that all 4 blocks where base will stand are empty and inside the map
        if (spawnPosArrayX < GAME_WIDTH / BLOCK_SIZE && spawnPosArrayY < GAME_HEIGHT / BLOCK_SIZE)
        {
            if (spawnPosArrayX + 1 < GAME_WIDTH / BLOCK_SIZE && spawnPosArrayY < GAME_HEIGHT / BLOCK_SIZE)
            {
                if (spawnPosArrayX < GAME_WIDTH / BLOCK_SIZE && spawnPosArrayY + 1 < GAME_HEIGHT / BLOCK_SIZE)
                {
                    if (spawnPosArrayX + 1 < GAME_WIDTH / BLOCK_SIZE && spawnPosArrayY + 1 < GAME_HEIGHT / BLOCK_SIZE)
                    {
                        if (collisionMatrix[spawnPosArrayX][spawnPosArrayY] == null && collisionMatrix[spawnPosArrayX + 1][spawnPosArrayY] == null &&
                                collisionMatrix[spawnPosArrayX][spawnPosArrayY + 1] == null && collisionMatrix[spawnPosArrayX + 1][spawnPosArrayY + 1] == null)
                        {
                            base = new Base(gamePane, spawnPosArrayX, spawnPosArrayY, lifePoints, collisionMatrix);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }


    //////////////////////////////SCREENS////////////////////////////////////////
    private void showLoseScreen()
    {
        isGamePaused = true;
        createShadowOverlay();
        createGamePanel();

        InfoLabel youLoseLabel = new InfoLabel("YOU LOSE!", ((double) GAME_WIDTH / 2 - 100), GAME_HEIGHT / 2 - 180, 40);
        gamePane.getChildren().add(youLoseLabel);
    }

    private void showWinScreen()
    {
        isGamePaused = true;
        createShadowOverlay();
        createGamePanel();
        InfoLabel youLoseLabel = new InfoLabel("YOU WON!", ((double) GAME_WIDTH / 2 - 100), GAME_HEIGHT / 2 - 180, 40);
        gamePane.getChildren().add(youLoseLabel);
    }
}
