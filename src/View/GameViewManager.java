package View;
// TODO  collision system,

// TODO  shooting system,

// TODO  enemies

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;


public class GameViewManager {
    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private Stage menuStage;
    private ImageView playerOneTank;

    //variables for animation
    private boolean isLeftKeyPressed;
    private boolean isRightKeyPressed;
    private boolean isUpKeyPressed;
    private boolean isDownKeyPressed;
    private boolean isSpaceKeyPressed;  //shoot key
    private int angle;  //current angle of first player tank
    private AnimationTimer gameTimer;

    private final static int GAME_WIDTH = 800;  //Map divided into blocks 50x50 pixels each
    private final static int GAME_HEIGHT = 600; //Map has size 16x12 blocks
    private final static int BLOCK_SIZE = 50;

    public GameViewManager() {
        initializeStage();
        createKeyListeners();
        createBackground();
    }

    private void initializeStage() {
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
    }

    private void createBackground() {
        Image backgroundGameImage = new Image("View/resources/texture.png", BLOCK_SIZE, BLOCK_SIZE, true, true);
        BackgroundImage backgroundGame = new BackgroundImage(backgroundGameImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
        gamePane.setBackground(new Background(backgroundGame));
    }

    //Creating Listeners to inform which buttons are pressed - used to determine which animation is called
    private void createKeyListeners() {
        gameScene.setOnKeyPressed(event -> {  //lambda function to handle event
            if(event.getCode() == KeyCode.LEFT) {
                isLeftKeyPressed = true;
            }
            else if (event.getCode() == KeyCode.RIGHT) {
                isRightKeyPressed = true;
            }
            else if (event.getCode() == KeyCode.UP) {
                isUpKeyPressed = true;
            }
            else if (event.getCode() == KeyCode.DOWN) {
                isDownKeyPressed = true;
            }
            else if (event.getCode() == KeyCode.SPACE) {
                isSpaceKeyPressed = true;
            }

        });

        gameScene.setOnKeyReleased( event -> {
            if(event.getCode() == KeyCode.LEFT) {
                isLeftKeyPressed = false;
            }
            else if (event.getCode() == KeyCode.RIGHT) {
                isRightKeyPressed = false;
            }
            else if (event.getCode() == KeyCode.UP) {
                isUpKeyPressed = false;
            }
            else if (event.getCode() == KeyCode.DOWN) {
                isDownKeyPressed = false;
            }
            else if (event.getCode() == KeyCode.SPACE) {
                isSpaceKeyPressed = false;
            }
        });
    }

    //showing game window
    public void createGame(Stage menuStage, boolean twoPlayersMode) {
        this.menuStage = menuStage;
        this.menuStage.hide();
        createTank();
        createGameLoop();
        gameStage.show();
    }

    //Timer to call animation in every frame
    private void createGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveTank();
            }
        };
        gameTimer.start();
    }

    private void createTank() {
        playerOneTank = new ImageView("View/resources/tank_red.png");
        playerOneTank.setLayoutX(GAME_WIDTH/2);
        playerOneTank.setLayoutY(GAME_HEIGHT-50);
        gamePane.getChildren().add(playerOneTank);
        angle = 0; //starting angle
    }

    //////////////////////////ANIMATIONS AND TANK MOTION////////////////////////////////////
    private void moveTank() {
        //TODO make sure that tank moves only by squares

        //Checking if only one key is pressed
        if(isLeftKeyPressed && !isRightKeyPressed && !isUpKeyPressed && !isDownKeyPressed) {
                if (angle > -90 && angle <= 90) {     //checking angle of tank do select rotation direction - 1st & 4th quarter of circle
                    angle -= 10;
                }
                if (angle < -90 || angle >= 90) {     //3rd & 4th quarter
                    angle += 10;
                }
                if (angle == 190)                    //if passed 180 degrees point, change to minus half
                    angle = -170;
                else if (angle == -190)              //if passed -180 degrees point, change to plus half
                    angle = 170;

                playerOneTank.setRotate(angle);
                if (playerOneTank.getLayoutX() > 0.0) {     //movement if tank is still in game area
                    System.out.println("X=" + playerOneTank.getLayoutX());
                    playerOneTank.setLayoutX(playerOneTank.getLayoutX() - 5);
                }
        }

        if(isRightKeyPressed && !isLeftKeyPressed && !isUpKeyPressed && !isDownKeyPressed) {
            if(angle >=-90 && angle < 90) { //checking angle of tank do select rotation direction - 1st & 4th quarter
                angle +=10;
            }
            if(angle <=-90 || angle > 90) { //3rd & 4th quarter
                angle -=10;
            }
            if(angle == 190)                //if passed 180 degrees point, change to minus half
                angle = -170;
            else if(angle == -190)          //if passed -180 degrees point, change to plus half
                angle = 170;

            playerOneTank.setRotate(angle);
            if (playerOneTank.getLayoutX() < GAME_WIDTH-50) { //movement if tank is still in game area
                playerOneTank.setLayoutX(playerOneTank.getLayoutX()+5);
            }
        }

        if(isUpKeyPressed && !isDownKeyPressed && !isLeftKeyPressed && !isRightKeyPressed) {
            if(angle >=-180 && angle < 0) {  //checking angle of tank do select rotation direction - 3rd & 4th quarter
                angle +=10;
            }
            if(angle >0 && angle <= 180) {  //1st & 2nd quarter
                angle -=10;
            }
                playerOneTank.setRotate(angle);
            if (playerOneTank.getLayoutY() > 0) {   //movement if tank is still in game area
                playerOneTank.setLayoutY(playerOneTank.getLayoutY()-5);
            }
        }

        if(isDownKeyPressed && !isUpKeyPressed && !isLeftKeyPressed && !isRightKeyPressed) {
            if(angle < 180 && angle >= 0) {  //checking angle of tank do select rotation direction - 3rd & 4th quarter
                angle +=10;
            }
            if(angle > -180 && angle < 0) {  //movement if tank is still in game area
                angle -=10;
            }
            if(angle >=-180 && angle <= 180)
                playerOneTank.setRotate(angle);
            if (playerOneTank.getLayoutY() < GAME_HEIGHT-50) {  //movement if tank is still in game area
                System.out.println(playerOneTank.getLayoutY());
                playerOneTank.setLayoutY(playerOneTank.getLayoutY()+5);
            }
        }
    }

}
