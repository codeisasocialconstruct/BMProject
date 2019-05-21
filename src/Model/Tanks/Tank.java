package Model.Tanks;

import View.DataBaseConnector;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tank {
    private AnchorPane gamePane;
    private ImageView tankSprite;
    private static int nextID = 0;
    protected int lifePoints;
    protected int ID;

    protected boolean fullSpin;
    protected int angle;  //current angle of tank
    protected char directionOfMovement;
    protected int moveIterator;
    protected static String[][] positionMatrix;
    protected int currentX; //current X position in positionMatrix
    protected int currentY; //current Y position in positionMatrix
    protected boolean allowedToMove; //For checking if congruent block is empty
    protected List<Tank> tankList;
    protected List<Projectile> listOfActiveProjectiles;
    protected Projectile projectile;
    protected int shootDelay;

    protected static int GAME_WIDTH;  //Map divided into blocks 50x50 pixels each
    protected static int GAME_HEIGHT; //Map size is 16x12 blocks
    protected static int BLOCK_SIZE;
    protected DataBaseConnector dbConnector;

    public Tank(AnchorPane gamePane, int spawnPosArrayX, int spawnPosArrayY, String tankSpriteUrl, List<Tank> tankList,
                String[][] collisionMatrix, int maxLifePoints, DataBaseConnector dbConnector) {
        this.gamePane = gamePane;
        positionMatrix = collisionMatrix; //passing position matrix through reference
        ID = nextID;             //generating new ID
        nextID++;
        tankList.add(this);               //adding tank to tanks list

        collisionMatrix[spawnPosArrayX][spawnPosArrayY] = Integer.toString(ID); //saving tank position in collision matrix
        currentX = spawnPosArrayX;
        currentY = spawnPosArrayY;

        tankSprite = new ImageView(tankSpriteUrl);  //loading sprite
        tankSprite.setLayoutX(spawnPosArrayX*50);
        tankSprite.setLayoutY(spawnPosArrayY*50);

        gamePane.getChildren().add(tankSprite);

        lifePoints = maxLifePoints;
        angle = 0; //starting angle
        moveIterator = 0;
        this.tankList = tankList;
        listOfActiveProjectiles = new ArrayList<>(); //creating arraylist to manage projectiles created by this tank
        shootDelay = 0;
        this.dbConnector = dbConnector;
        GAME_HEIGHT = dbConnector.getGame_height();
        GAME_WIDTH = dbConnector.getGame_width();
        BLOCK_SIZE = dbConnector.getBlock_size();
    }

    public int getID() {return ID;}

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    //////////////////////////////////COLLISION SYSTEM////////////////////////////////////
    protected boolean checkIfDownEmpty() {
        if (currentY < GAME_HEIGHT/BLOCK_SIZE-1) {
            if (positionMatrix[currentX][currentY + 1] == null)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    protected boolean checkIfUpEmpty() {
        if (currentY > 0) {
            if (positionMatrix[currentX][currentY - 1] == null)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    protected boolean checkIfRightEmpty() {
        if (currentX < GAME_WIDTH/BLOCK_SIZE-1) {
            if (positionMatrix[currentX + 1][currentY] == null)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    protected boolean checkIfLeftEmpty() {
        if(currentX>0) {
            if (positionMatrix[currentX - 1][currentY] == null)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    //////////////////////////ANIMATIONS AND TANK MOTION////////////////////////////////////
    protected boolean moveTankDownOneIteration() {
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
            tankSprite.setRotate(angle);

        //movement if tank is still in game area and congruent block is empty
        if (tankSprite.getLayoutY() < GAME_HEIGHT-BLOCK_SIZE && allowedToMove ) {
            tankSprite.setLayoutY(tankSprite.getLayoutY()+5);
            return true;
        }
        else
            return false;
    }

    protected boolean moveTankUpOneIteration() {
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
        tankSprite.setRotate(angle);

        //movement if tank is still in game area and congruent block is empty
        if (tankSprite.getLayoutY() > 0 && allowedToMove) {
            tankSprite.setLayoutY(tankSprite.getLayoutY()-5);
            return true;
        }
        else
            return false;
    }

    protected boolean moveTankRightOneIteration() {
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

        //movement if tank is still in game area and congruent block is empty
        tankSprite.setRotate(angle);
        if (tankSprite.getLayoutX() < GAME_WIDTH-BLOCK_SIZE && allowedToMove) {
            tankSprite.setLayoutX(tankSprite.getLayoutX()+5);
            return true;
        }
        else
            return false;
    }

    protected boolean moveTankLeftOneIteration() {
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

        tankSprite.setRotate(angle);

        //movement if tank is still in game area and congruent block is empty
        if (tankSprite.getLayoutX() > 0.0 && allowedToMove) {
            tankSprite.setLayoutX(tankSprite.getLayoutX() - 5);
            return true;
        }
        else
            return false;
    }

    private void startTankMovement() {
        Random rand = new Random();

        // Obtain a random number
        int n = rand.nextInt(100);
        if(n==0) {
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

        if(n==1) {
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

        if(n==2) {
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

        if(n==3) {
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
        if(moveIterator>0)
            continueTankMovement();
        else
            startTankMovement();
    }


    /////////////////////////////SHOOTING//////////////////////////////////////
    public boolean shoot() {
        if(angle == 90)
                projectile = new Projectile(gamePane, currentX, currentY, positionMatrix, 'R', listOfActiveProjectiles, tankList);
        else if(angle == -90) {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix, 'L', listOfActiveProjectiles,tankList);
        }
        else if(angle == 0) {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix, 'U', listOfActiveProjectiles,tankList);
        }
        else if(angle == -180 || angle == 180) {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix, 'D', listOfActiveProjectiles,tankList);
        }

        return false;
    }

    public void moveProjectiles() {
        if(shootDelay==0)
            shoot();

        for (int x = 0; x<listOfActiveProjectiles.size(); x++) {
            listOfActiveProjectiles.get(x).moveProjectile();
            if( listOfActiveProjectiles.get(x).getHitConfirmed())
                listOfActiveProjectiles.remove(x);
        }

        if(shootDelay==30)
            shootDelay=0;
        else
            shootDelay++;
    }

    ///////////////////////////LIFE POINTS AND TANK DESTRUCTION/////////////////////
    public int getLifePoints() {return lifePoints;}

    public void takeDamage() {
        lifePoints--;
    }

    public void tankDestruction() {
        positionMatrix[currentX][currentY] = null;
        for (Projectile projectile: listOfActiveProjectiles) {
            projectile.hideProjectile();
        }
        listOfActiveProjectiles.clear();
        gamePane.getChildren().remove(tankSprite);
    }

    private void destructionAnimation() {}//TODO destruction animation
}
