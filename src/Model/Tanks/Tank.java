package Model.Tanks;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.List;
import java.util.Random;

public class Tank {
    private AnchorPane gamePane;
    private ImageView tankSprite;

    protected int lifePoints;

    protected boolean fullSpin;
    protected int angle;  //current angle of first player tank
    protected char directionOfMovement;
    protected int moveIterator;

    private final static int GAME_WIDTH = 800;  //Map divided into blocks 50x50 pixels each
    private final static int GAME_HEIGHT = 600; //Map has size 16x12 blocks
    private final static int BLOCK_SIZE = 50;

    public Tank(AnchorPane gamePane, int spawnPosX, int spawnPosY, String tankSpriteUrl, List<Tank> tankList) {
        this.gamePane = gamePane;
        tankSprite = new ImageView(tankSpriteUrl);
        tankSprite.setLayoutX(spawnPosX);
        tankSprite.setLayoutY(spawnPosY);
        gamePane.getChildren().add(tankSprite);

        tankList.add(this);
        angle = 0; //starting angle
        moveIterator = 0;
    }

    protected void moveTankDownOneIteration() {
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
        if (tankSprite.getLayoutY() < GAME_HEIGHT-BLOCK_SIZE) {  //movement if tank is still in game area
            tankSprite.setLayoutY(tankSprite.getLayoutY()+5);
        }
    }

    protected void moveTankUpOneIteration() {
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
        if (tankSprite.getLayoutY() > 0) {   //movement if tank is still in game area
            tankSprite.setLayoutY(tankSprite.getLayoutY()-5);
        }
    }

    protected void moveTankRightOneIteration() {
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

        tankSprite.setRotate(angle);
        if (tankSprite.getLayoutX() < GAME_WIDTH-BLOCK_SIZE) { //movement if tank is still in game area
            tankSprite.setLayoutX(tankSprite.getLayoutX()+5);
        }
    }

    protected void moveTankLeftOneIteration() {
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

        if (tankSprite.getLayoutX() > 0.0) {     //movement if tank is still in game area
            tankSprite.setLayoutX(tankSprite.getLayoutX() - 5);
        }
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
            moveTankLeftOneIteration();
            moveIterator = 9;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if(n==1) {
            if(angle == -90)
                fullSpin=true;
            else
                fullSpin=false;
            directionOfMovement = 'R';  //giving direction to continue movement
            moveTankRightOneIteration();
            moveIterator = 9;            //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if(n==2) {
            if(angle == -180 || angle == 180)
                fullSpin=true;
            else
                fullSpin=false;
            directionOfMovement = 'U';  //giving direction to continue movement
            moveTankUpOneIteration();
            moveIterator = 9;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if(n==3) {
            if(angle == 0)
                fullSpin=true;
            else
                fullSpin=false;
            directionOfMovement = 'D';  //giving direction to continue movement
            moveTankDownOneIteration();
            moveIterator = 9;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
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
}
