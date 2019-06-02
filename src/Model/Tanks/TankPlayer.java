package Model.Tanks;

import Model.MapElements.Base;
import Model.MapElements.BrickBlock;
import View.DataBaseConnector;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;

import java.util.ArrayList;
import java.util.List;

public class TankPlayer extends Tank{

    private Scene gameScene;

    private boolean isLeftKeyPressed;
    private boolean isRightKeyPressed;
    private boolean isUpKeyPressed;
    private boolean isDownKeyPressed;
    private boolean isShootKeyPressed;  //shoot key

    private boolean isLeftSecondPlayerKeyPressed;
    private boolean isRightSecondPlayerKeyPressed;
    private boolean isUpSecondPlayerKeyPressed;
    private boolean isDownSecondPlayerKeyPressed;
    private boolean isShootSecondPlayerKeyPressed;  //shoot key


    private boolean isPaused;
    private boolean twoPlayersMode;

    private KeyCode moveLeftKey;
    private KeyCode moveRightKey;
    private KeyCode moveUpKey;
    private KeyCode moveDownKey;
    private KeyCode shootKey;

    private KeyCode moveLeftSecondPlayerKey;
    private KeyCode moveRightSecondPlayerKey;
    private KeyCode moveUpSecondPlayerKey;
    private KeyCode moveDownSecondPlayerKey;
    private KeyCode shootSecondPlayerKey;

    private List<ImageView> lifePointIndicator;
    private final static String HEART_SPRITE_FULL = "Model/Resources/tankSprites/heart_full.png";
    private final static String HEART_SPRITE_EMPTY = "Model/Resources/tankSprites/heart_empty.png";

    TankSecondPlayer secondPlayer;


    public TankPlayer(AnchorPane gamePane, Scene gameScene, int spawnPosArrayX, int spawnPosArrayY, String tankSpriteUrl, List<Tank> tankList,
                      String[][] collisionMatrix, Base base, DataBaseConnector dataBaseConnector,
                      ArrayList<BrickBlock> brickList,ArrayList<ImageView> waterList, boolean twoPlayersMode) {
        super(gamePane, spawnPosArrayX, spawnPosArrayY, tankSpriteUrl, tankList, collisionMatrix, 5, base,dataBaseConnector, brickList, waterList);

        this.gameScene = gameScene;
        this.twoPlayersMode = twoPlayersMode;
        this.moveLeftKey = KeyCode.LEFT;
        this.moveRightKey = KeyCode.RIGHT;
        this.moveUpKey = KeyCode.UP;
        this.moveDownKey = KeyCode.DOWN;
        this.shootKey = KeyCode.CONTROL;

        if(twoPlayersMode) {
            if(checkIfLeftEmpty())
                secondPlayer = new TankSecondPlayer(gamePane, spawnPosArrayX-1, spawnPosArrayY, tankList, collisionMatrix,
                        base, dataBaseConnector, brickList, waterList);
            else if(checkIfRightEmpty())
                secondPlayer = new TankSecondPlayer(gamePane, spawnPosArrayX+1, spawnPosArrayY, tankList, collisionMatrix,
                        base, dataBaseConnector, brickList, waterList);
            else if(checkIfUpEmpty())
                secondPlayer = new TankSecondPlayer(gamePane, spawnPosArrayX, spawnPosArrayY-1, tankList, collisionMatrix,
                        base, dataBaseConnector, brickList, waterList);
            else if(checkIfDownEmpty())
                secondPlayer = new TankSecondPlayer(gamePane, spawnPosArrayX+1, spawnPosArrayY, tankList, collisionMatrix,
                        base, dataBaseConnector, brickList, waterList);
            else
                secondPlayer = new TankSecondPlayer(gamePane, spawnPosArrayX, spawnPosArrayY, tankList, collisionMatrix,
                        base, dataBaseConnector, brickList, waterList);

            this.moveLeftSecondPlayerKey = KeyCode.A;
            this.moveRightSecondPlayerKey = KeyCode.D;
            this.moveUpSecondPlayerKey = KeyCode.W;
            this.moveDownSecondPlayerKey = KeyCode.S;
            this.shootSecondPlayerKey = KeyCode.F;
        }

        createKeyListeners();

        lifePointIndicator = new ArrayList<>();
        createLifeIndicator();

        isPaused = false;
    }

    //Creating Listeners to inform which buttons are pressed - used to determine which animation is called
    private void createKeyListeners() {
        gameScene.setOnKeyPressed(event -> {    //lambda function to handle key pressing event
            if(event.getCode() == moveLeftKey) {
                isLeftKeyPressed = true;
            }
            else if (event.getCode() == moveRightKey) {
                isRightKeyPressed = true;

            }
            else if (event.getCode() == moveUpKey) {
                isUpKeyPressed = true;

            }
            else if (event.getCode() == moveDownKey) {
                isDownKeyPressed = true;
            }

            if (event.getCode() == shootKey) {
                isShootKeyPressed = true;
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                isPaused = true;
            }

            if (twoPlayersMode) {
                if (event.getCode() == moveLeftSecondPlayerKey)
                    isLeftSecondPlayerKeyPressed = true;
                else if (event.getCode() == moveRightSecondPlayerKey)
                    isRightSecondPlayerKeyPressed = true;
                else if (event.getCode() == moveUpSecondPlayerKey)
                    isUpSecondPlayerKeyPressed = true;
                else if(event.getCode() == moveDownSecondPlayerKey)
                    isDownSecondPlayerKeyPressed = true;

                if (event.getCode() == shootSecondPlayerKey)
                    isShootSecondPlayerKeyPressed = true;
            }
        });

        gameScene.setOnKeyReleased( event -> {
            if(event.getCode() == moveLeftKey) {
                isLeftKeyPressed = false;
            }
            else if (event.getCode() == moveRightKey) {
                isRightKeyPressed = false;
            }
            else if (event.getCode() == moveUpKey) {
                isUpKeyPressed = false;
            }
            else if (event.getCode() == moveDownKey) {
                isDownKeyPressed = false;
            }
            if (event.getCode() == shootKey) {
                isShootKeyPressed = false;
            }

            if (twoPlayersMode) {
                if (event.getCode() == moveLeftSecondPlayerKey)
                    isLeftSecondPlayerKeyPressed = false;
                else if (event.getCode() == moveRightSecondPlayerKey)
                    isRightSecondPlayerKeyPressed = false;
                else if (event.getCode() == moveUpSecondPlayerKey)
                    isUpSecondPlayerKeyPressed = false;
                else if(event.getCode() == moveDownSecondPlayerKey)
                    isDownSecondPlayerKeyPressed = false;

                if (event.getCode() == shootSecondPlayerKey)
                    isShootSecondPlayerKeyPressed = false;
            }
        });
    }

    //////////////////////////PAUSING GAME/////////////////////////////////////////////
    public boolean getIsPaused() {return isPaused;}

    public void setIsPaused(boolean isPaused) {this.isPaused = isPaused;}

    //////////////////////////ANIMATIONS AND TANK MOTION////////////////////////////////////

    private void startTankMovement() {
        //Checking if only one key is pressed
        if(isLeftKeyPressed && !isRightKeyPressed && !isUpKeyPressed && !isDownKeyPressed) {
            if(angle == 90)
                fullSpin=true;
            else
                fullSpin=false;

            directionOfMovement = 'L';  //giving direction to continue movement
            allowedToMove = checkIfLeftEmpty();
            if (moveTankLeftOneIteration()) {
                positionMatrix[currentX-1][currentY]=Integer.toString(ID);
                positionMatrix[currentX][currentY]=null;
                currentX--;
            }
            moveIterator = BLOCK_SIZE/5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if(isRightKeyPressed && !isLeftKeyPressed && !isUpKeyPressed && !isDownKeyPressed) {
            if(angle == -90)
                fullSpin=true;
            else
                fullSpin=false;

            directionOfMovement = 'R';  //giving direction to continue movement
            allowedToMove = checkIfRightEmpty();
            if (moveTankRightOneIteration()) {
                positionMatrix[currentX+1][currentY]=Integer.toString(ID);
                positionMatrix[currentX][currentY]=null;
                currentX++;
            }
            moveIterator = BLOCK_SIZE/5 - 1;            //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if(isUpKeyPressed && !isDownKeyPressed && !isLeftKeyPressed && !isRightKeyPressed) {
            if(angle == -180 || angle == 180)
                fullSpin=true;
            else
                fullSpin=false;

            directionOfMovement = 'U';  //giving direction to continue movement
            allowedToMove = checkIfUpEmpty();
            if (moveTankUpOneIteration()) {
                positionMatrix[currentX][currentY-1]=Integer.toString(ID);
                positionMatrix[currentX][currentY]=null;
                currentY--;
            }
            moveIterator = BLOCK_SIZE/5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if(isDownKeyPressed && !isUpKeyPressed && !isLeftKeyPressed && !isRightKeyPressed) {
            if(angle == 0)
                fullSpin=true;
            else
                fullSpin=false;

            directionOfMovement = 'D';  //giving direction to continue movement
            allowedToMove = checkIfDownEmpty();
            if (moveTankDownOneIteration()) {
                positionMatrix[currentX][currentY+1]=Integer.toString(ID);
                positionMatrix[currentX][currentY]=null;
                currentY++;
            }
            moveIterator = BLOCK_SIZE/5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
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

    public void moveTank() {
        if(moveIterator>0 && lifePoints>0)
            continueTankMovement();
        else
            startTankMovement();

        if (twoPlayersMode && secondPlayer.getLifePoints()>0)
            secondPlayer.moveTank(isLeftSecondPlayerKeyPressed, isRightSecondPlayerKeyPressed, isUpSecondPlayerKeyPressed, isDownSecondPlayerKeyPressed);
    }

    ///////////////////////////////////SHOOTING////////////////////////////
    public void moveProjectiles() {
        if(isShootKeyPressed && shootDelayTimer.getCanShoot() && lifePoints>0) {
            shoot();
            shootDelayTimer.afterShootDelay(400);
        }

        for (int x = 0; x<listOfActiveProjectiles.size(); x++) {
            listOfActiveProjectiles.get(x).moveProjectile();
            if( listOfActiveProjectiles.get(x).getHitConfirmed())
                listOfActiveProjectiles.remove(x);  //if projectile hit anything it is deleted
        }

        if(twoPlayersMode && secondPlayer.getLifePoints()>0)
            secondPlayer.moveProjectiles(isShootSecondPlayerKeyPressed);
    }

    ///////////////////////////////////DAMAGE AND LIFE POINTS////////////////////////////

    private void createLifeIndicator() {
        ImageView heart;
        for (int iterator = 0; iterator < lifePoints; iterator++) {
            heart = new ImageView(HEART_SPRITE_FULL);
            heart.setLayoutX(GAME_WIDTH - (BLOCK_SIZE*(iterator+1))+8);
            heart.setLayoutY(8);
            lifePointIndicator.add(heart);
            gamePane.getChildren().add(heart);
        }
    }

    private void lifeIndicatorEmptyHeart() {
        Image emptyHeart = new Image(HEART_SPRITE_EMPTY);
        lifePointIndicator.get(lifePoints-1).setImage(emptyHeart);
    }

    public void heartsToFront()
    {
        for(ImageView heart: lifePointIndicator)
        {
            heart.toFront();
        }
        if (twoPlayersMode)
            secondPlayer.heartsToFront();
    }

    void takeDamage() {
        lifeIndicatorEmptyHeart(); //empty one heart
        lifePoints--;

        playHitSound();
        hitAnimation();
    }

    public int getSecondPlayerLifePoints() {
        return secondPlayer.getLifePoints();
    }

}
