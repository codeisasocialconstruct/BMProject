package View;
// TODO create Tank class and PlayerTank class, move methods there

// TODO  collision system,

// TODO  shooting system,

// TODO  enemies

import Model.NavigationButton;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


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
    private boolean fullSpin;
    private int angle;  //current angle of first player tank
    private char directionOfMovement;
    private int moveIterator;
    private AnimationTimer gameTimer;

    private boolean gridMode;

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
    private void createKeyListeners() {
        gameScene.setOnKeyPressed(event -> {    //lambda function to handle key pressing event
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

            if(event.getCode() == KeyCode.F11) {    //turning on grid mode
                gridMode = true;
                createBackground();
            }

            if(event.getCode() == KeyCode.F12) {    //turning off grid mode
                gridMode = false;
                createBackground();
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
        this.menuStage = menuStage;
        this.menuStage.hide();
        createExitButton();
        createTank();
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
                if(moveIterator>0)
                    continueTankMovement();
                else
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
        moveIterator = 0;
    }

    //////////////////////////ANIMATIONS AND TANK MOTION////////////////////////////////////
    private void moveTank() {
        //Checking if only one key is pressed
        if(isLeftKeyPressed && !isRightKeyPressed && !isUpKeyPressed && !isDownKeyPressed) {
            if(angle == 90)
                fullSpin=true;
            else
                fullSpin=false;
            directionOfMovement = 'L';  //giving direction to continue movement
            moveTankLeftOneIteration();
            moveIterator = 9;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of moveTank
        }

        if(isRightKeyPressed && !isLeftKeyPressed && !isUpKeyPressed && !isDownKeyPressed) {
            if(angle == -90)
                fullSpin=true;
            else
                fullSpin=false;
            directionOfMovement = 'R';  //giving direction to continue movement
            moveTankRightOneIteration();
            moveIterator = 9;            //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of moveTank
        }

        if(isUpKeyPressed && !isDownKeyPressed && !isLeftKeyPressed && !isRightKeyPressed) {
            if(angle == -180 || angle == 180)
                fullSpin=true;
            else
                fullSpin=false;
            directionOfMovement = 'U';  //giving direction to continue movement
            moveTankUpOneIteration();
            moveIterator = 9;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of moveTank
        }

        if(isDownKeyPressed && !isUpKeyPressed && !isLeftKeyPressed && !isRightKeyPressed) {
            if(angle == 0)
                fullSpin=true;
            else
                fullSpin=false;
            directionOfMovement = 'D';  //giving direction to continue movement
            moveTankDownOneIteration();
            moveIterator = 9;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of moveTank
        }
    }

    private void continueTankMovement() {   //function to continue movement started by pressing button, that make sure tank moves only by 50 pixels
        moveIterator--;

        if(directionOfMovement =='L')
            moveTankLeftOneIteration();

        if(directionOfMovement =='R')
            moveTankRightOneIteration();

        if(directionOfMovement == 'U')
            moveTankUpOneIteration();

        if(directionOfMovement == 'D')
            moveTankDownOneIteration();
    }

    private void moveTankDownOneIteration() {
        if(angle < 180 && angle >= 0) {  //checking angle of tank do select rotation direction - 3rd & 4th quarter
            if(fullSpin)
                angle +=18;
            else
                angle +=10;
        }
        if(angle > -180 && angle < 0) {  //movement if tank is still in game area
            if(fullSpin)
                angle -=18;
            else
                angle -=10;
        }
        if(angle >=-180 && angle <= 180)
            playerOneTank.setRotate(angle);
        if (playerOneTank.getLayoutY() < GAME_HEIGHT-50) {  //movement if tank is still in game area
            System.out.println(playerOneTank.getLayoutY());
            playerOneTank.setLayoutY(playerOneTank.getLayoutY()+5);
        }
    }

    private void moveTankUpOneIteration() {
        if(angle >=-180 && angle < 0) {  //checking angle of tank do select rotation direction - 3rd & 4th quarter
            if(fullSpin)
                angle +=18;             //changing angle rotation due to fullSpin, makes sure that spin will be complete after 10 frames
            else
                angle +=10;
        }
        if(angle >0 && angle <= 180) {  //1st & 2nd quarter
            if(fullSpin)
                angle -=18;             //changing angle rotation due to fullSpin, makes sure that spin will be complete after 10 frames
            else
                angle -=10;
        }
        playerOneTank.setRotate(angle);
        if (playerOneTank.getLayoutY() > 0) {   //movement if tank is still in game area
            playerOneTank.setLayoutY(playerOneTank.getLayoutY()-5);
        }
    }

    private void moveTankRightOneIteration() {
        if(angle >=-90 && angle < 90) { //checking angle of tank do select rotation direction - 1st & 4th quarter
            if(fullSpin)
                angle +=18;             //changing angle rotation due to fullSpin, makes sure that spin will be complete after 10 frames
            else
                angle +=10;
        }
        if(angle <=-90 || angle > 90) { //3rd & 4th quarter
            if(fullSpin)
                angle -=18;
            else
                angle -=10;
        }
        if(angle > 180)                //if passed 180 degrees point, change to minus half
            angle -= 360;
        else if(angle < -180)          //if passed -180 degrees point, change to plus half
            angle += 360;

        playerOneTank.setRotate(angle);
        if (playerOneTank.getLayoutX() < GAME_WIDTH-50) { //movement if tank is still in game area
            playerOneTank.setLayoutX(playerOneTank.getLayoutX()+5);
        }
    }

    private void moveTankLeftOneIteration() {
        if (angle > -90 && angle <= 90) {     //checking angle of tank do select rotation direction - 1st & 4th quarter of circle
            if(fullSpin)
                angle -= 18;
            else
                angle -= 10;
        }
        if (angle < -90 || angle >= 90) {     //3rd & 4th quarter
            if(fullSpin)        //if tank is spinning 180 degrees for each frame it needs to spin by 18 degrees
                angle+=18;
            else
                angle += 10;
        }
        if (angle > 180)                    //if passed 180 degrees point, change to minus half
            angle -= 360;
        else if (angle < -180)              //if passed -180 degrees point, change to plus half
            angle += 360;

        playerOneTank.setRotate(angle);

        if (playerOneTank.getLayoutX() > 0.0) {     //movement if tank is still in game area
            System.out.println("X=" + playerOneTank.getLayoutX());
            playerOneTank.setLayoutX(playerOneTank.getLayoutX() - 5);
        }
    }
}
