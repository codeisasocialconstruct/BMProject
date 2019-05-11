package Model.Tanks;

import Model.MapElements.Base;
import Model.SpriteAnimation;
import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

import java.util.*;

public class Tank {
    AnchorPane gamePane;
    ImageView tankSprite;
    private static int nextID = 0;
    int lifePoints;
    int ID;

    boolean fullSpin;
    int angle;  //current angle of tank
    char directionOfMovement;
    int moveIterator;
    static String[][] positionMatrix;
    int currentX; //current X position in positionMatrix
    int currentY; //current Y position in positionMatrix
    boolean allowedToMove; //For checking if congruent block is empty
    Base base;
    List<Tank> tankList;
    List<Projectile> listOfActiveProjectiles;
    Projectile projectile;
    ShootDelayTimer shootDelayTimer;
    Random shootChance;
    AudioClip sounds;

    ImageView tankExplosion;
    final static String EXPLOSION_SPRITE_SHEET = "Model/Resources/tankSprites/TankExplosionSpriteSheet.png";
    final static String SHOOT_SOUND = "../Resources/TankSounds/shoot_sound.wav";
    final static String HIT_SOUND = "../Resources/TankSounds/get_hit_sound.wav";
    final static String TANK_EXPLOSION_SOUND = "../Resources/TankSounds/tank_explosion_sound.wav";

    final static int GAME_WIDTH = 800;  //Map divided into blocks 50x50 pixels each
    final static int GAME_HEIGHT = 600; //Map size is 16x12 blocks
    final static int BLOCK_SIZE = 50;

    public Tank(AnchorPane gamePane, int spawnPosArrayX, int spawnPosArrayY, String tankSpriteUrl, List<Tank> tankList,
                String[][] collisionMatrix, int maxLifePoints, Base base) {
        this.gamePane = gamePane;
        positionMatrix = collisionMatrix; //passing position matrix through reference
        ID = nextID;             //generating new ID
        nextID++;
        tankList.add(this);               //adding tank to tanks list
        this.tankList = tankList;

        collisionMatrix[spawnPosArrayX][spawnPosArrayY] = Integer.toString(ID); //saving tank position in collision matrix
        currentX = spawnPosArrayX;
        currentY = spawnPosArrayY;

        tankSprite = new ImageView(tankSpriteUrl);  //loading sprite
        tankSprite.setClip(new ImageView(tankSpriteUrl));   //deleting background from sprite
        tankSprite.setLayoutX(spawnPosArrayX*50);
        tankSprite.setLayoutY(spawnPosArrayY*50);

        gamePane.getChildren().add(tankSprite);

        if (lifePoints<1)
            this.lifePoints = 5;
        else
            this.lifePoints = maxLifePoints;

        angle = 0; //starting angle
        moveIterator = 0;
        listOfActiveProjectiles = new ArrayList<>(); //creating arraylist to manage projectiles created by this tank
        shootDelayTimer = new ShootDelayTimer();
        shootChance = new Random();
        this.base = base;
    }

    public int getID() {return ID;}

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    //////////////////////////////////COLLISION SYSTEM////////////////////////////////////
    boolean checkIfDownEmpty() {
        if (currentY < GAME_HEIGHT/BLOCK_SIZE-1) {
            if (positionMatrix[currentX][currentY + 1] == null)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    boolean checkIfUpEmpty() {
        if (currentY > 0) {
            if (positionMatrix[currentX][currentY - 1] == null)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    boolean checkIfRightEmpty() {
        if (currentX < GAME_WIDTH/BLOCK_SIZE-1) {
            if (positionMatrix[currentX + 1][currentY] == null)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    boolean checkIfLeftEmpty() {
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
    boolean moveTankDownOneIteration() {
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

    boolean moveTankUpOneIteration() {
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

    boolean moveTankRightOneIteration() {
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

    boolean moveTankLeftOneIteration() {
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
    boolean shoot() {
        if(angle == 90)
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix,
                    'R', listOfActiveProjectiles, tankList, base);
        else if(angle == -90) {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix,
                    'L', listOfActiveProjectiles,tankList, base);
        }
        else if(angle == 0) {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix,
                    'U', listOfActiveProjectiles,tankList, base);
        }
        else if(angle == -180 || angle == 180) {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix,
                    'D', listOfActiveProjectiles,tankList, base);
        }

        playShootSound();

        return false;
    }

    public void moveProjectiles() {

        if(shootDelayTimer.getCanShoot()) {
            if(shootChance.nextInt(100) > 90) {     //randomize shoot chance
                shoot();
                shootDelayTimer.afterShootDelay(600);   //calling timer to prevent non stop shooting
            }
        }

        for (int x = 0; x<listOfActiveProjectiles.size(); x++) {
            listOfActiveProjectiles.get(x).moveProjectile();
            if( listOfActiveProjectiles.get(x).getHitConfirmed())
                listOfActiveProjectiles.remove(x);
        }
    }

    ///////////////////////////LIFE POINTS AND TANK DESTRUCTION/////////////////////
    public int getLifePoints() {return lifePoints;}

    void takeDamage() {
        lifePoints--;
        playHitSound();
        hitAnimation();
    }

    void hitAnimation() {
        CoulorChangerTimer timer = new CoulorChangerTimer(tankSprite);
    }

    public void tankDestruction() {
        positionMatrix[currentX][currentY] = null;
        for (Projectile projectile: listOfActiveProjectiles) {
            projectile.hideProjectile();
        }
        playTankExplosionSound();

        destructionAnimation();
        listOfActiveProjectiles.clear();
        gamePane.getChildren().remove(tankSprite);
    }

    private void destructionAnimation() {
        tankExplosion = new ImageView(EXPLOSION_SPRITE_SHEET);
        tankExplosion.setFitWidth(2*BLOCK_SIZE);
        tankExplosion.setFitHeight(2*BLOCK_SIZE);
        tankExplosion.setLayoutX(tankSprite.getLayoutX() - BLOCK_SIZE/2); //placing animation
        tankExplosion.setLayoutY(tankSprite.getLayoutY()-BLOCK_SIZE/2);
        tankExplosion.setViewport(new Rectangle2D(0, 0, 128, 128)); //preventing from showing whole sprite sheet

        final Animation explosionAnimation = new SpriteAnimation(
                tankExplosion,
                Duration.millis(1000),
                12, 12,
                0, 0,
                128, 128
        );
        explosionAnimation.setCycleCount(1);
        explosionAnimation.setOnFinished(event -> gamePane.getChildren().remove(tankExplosion));    //removing sprite after animation is done
        gamePane.getChildren().add(tankExplosion);
        explosionAnimation.play();
    }


    ////////////////////////////////SOUNDS//////////////////////////////////////

    void playShootSound() {
        sounds = new AudioClip(this.getClass().getResource(SHOOT_SOUND).toExternalForm());
        sounds.setCycleCount(1);
        sounds.play(0.4);
    }

    void playHitSound() {
        sounds = new AudioClip(this.getClass().getResource(HIT_SOUND).toExternalForm());
        sounds.setCycleCount(1);
        sounds.play(0.4);
    }

    void playTankExplosionSound() {
        sounds = new AudioClip(this.getClass().getResource(TANK_EXPLOSION_SOUND).toExternalForm());
        sounds.setCycleCount(1);
        sounds.play(0.5);
    }
}
