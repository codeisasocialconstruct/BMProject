package Model.Tanks;

import Model.MapElements.Base;
import Model.MapElements.BrickBlock;
import View.DataBaseConnector;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

public class TankSecondPlayer extends Tank{

    private static final String TANK_SPRITE_URL= "Model/Resources/tankSprites/tankBlue.png";
    private final static String HEART_SPRITE_FULL = "Model/Resources/tankSprites/heart_full_blue.png";
    private final static String HEART_SPRITE_EMPTY = "Model/Resources/tankSprites/heart_empty.png";

    private List<ImageView> lifePointIndicator;


    public TankSecondPlayer(AnchorPane gamePane, int spawnPosX, int spawnPosY,
                            List<Tank> tankList, String[][] collisionMatrix, Base base, DataBaseConnector dataBaseConnector,
                            ArrayList<BrickBlock> brickList, ArrayList<ImageView> waterList) {

        super(gamePane, spawnPosX, spawnPosY, TANK_SPRITE_URL, tankList, collisionMatrix,
                5, base,dataBaseConnector, brickList, waterList);

        lifePointIndicator = new ArrayList<>();
        createLifeIndicator();
    }
    ///////////////////////////////////MOVEMENT////////////////////////////
    void startTankMovement(boolean isLeftKeyPressed, boolean isRightKeyPressed, boolean isUpKeyPressed, boolean isDownKeyPressed) {
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

    public void moveTank(boolean isLeftKeyPressed, boolean isRightKeyPressed, boolean isUpKeyPressed, boolean isDownKeyPressed) {
        if(moveIterator>0)
            continueTankMovement();
        else
            startTankMovement(isLeftKeyPressed, isRightKeyPressed, isUpKeyPressed, isDownKeyPressed);
    }

    ///////////////////////////////////SHOOTING////////////////////////////
    public void moveProjectiles(boolean isShootKeyPressed) {
        if(isShootKeyPressed && shootDelayTimer.getCanShoot()) {
            shoot();
            shootDelayTimer.afterShootDelay(400);
        }

        for (int x = 0; x<listOfActiveProjectiles.size(); x++) {
            listOfActiveProjectiles.get(x).moveProjectile();
            if( listOfActiveProjectiles.get(x).getHitConfirmed())
                listOfActiveProjectiles.remove(x);  //if projectile hit anything it is deleted
        }
    }

    ///////////////////////////////////DAMAGE////////////////////////////
    void takeDamage() {
        lifeIndicatorEmptyHeart(); //empty one heart
        lifePoints--;
        playHitSound();
        hitAnimation();
    }

    private void createLifeIndicator() {
        ImageView heart;
        for (int iterator = 0; iterator < lifePoints; iterator++) {
            heart = new ImageView(HEART_SPRITE_FULL);
            heart.setLayoutX(BLOCK_SIZE*(iterator)+8);
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
    }
}
