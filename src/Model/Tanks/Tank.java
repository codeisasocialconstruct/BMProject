package Model.Tanks;

import Model.MapElements.Base;
import Model.MapElements.BrickBlock;
import Model.SpriteAnimation;
import View.DataBaseConnector;
import View.GameViewManager;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

import java.util.*;

public class Tank extends Thread
{
    AnchorPane gamePane;
    ImageView tankSprite;
    private static int nextID = 0;
    int lifePoints;
    int ID;
    String tankType;
    boolean fullSpin;
    int angle;  //current angle of tank
    char directionOfMovement;
    int moveIterator;
    static String[][] positionMatrix;
    int currentX; //current X position in positionMatrix
    int currentY; //current Y position in positionMatrix
    boolean allowedToMove; //For checking if congruent block is empty
    boolean isAlive;
    Base base;
    List<Tank> tankList;
    List<Projectile> listOfActiveProjectiles;
    Projectile projectile;
    ShootDelayTimer shootDelayTimer;
    Random shootChance;
    AudioClip sounds;
    GameViewManager gameViewManager;
    ColorAdjust colorAdjust;
    ImageView tankExplosion;
    final static String EXPLOSION_SPRITE_SHEET = "Model/Resources/tankSprites/TankExplosionSpriteSheet.png";
    final static String SHOOT_SOUND = "../Resources/TankSounds/shoot_sound.wav";
    final static String HIT_SOUND = "../Resources/TankSounds/get_hit_sound.wav";
    final static String TANK_EXPLOSION_SOUND = "../Resources/TankSounds/tank_explosion_sound.wav";

    private DataBaseConnector dataBaseConnector;
    private ArrayList<BrickBlock> brickList;
    private ArrayList<ImageView> waterList;

    static int GAME_WIDTH;  //Map divided into blocks 50x50 pixels each
    static int GAME_HEIGHT; //Map size is 16x12 blocks
    final static int BLOCK_SIZE = 50;

    public Tank(String tankType, AnchorPane gamePane, int spawnPosArrayX, int spawnPosArrayY, String tankSpriteUrl, List<Tank> tankList,
                String[][] collisionMatrix, int maxLifePoints, Base base, DataBaseConnector dataBaseConnector, ArrayList<BrickBlock> brickList, ArrayList<ImageView> waterList, GameViewManager gameViewManager)
    {
        this.tankType = tankType;
        this.dataBaseConnector = dataBaseConnector;
        GAME_HEIGHT = dataBaseConnector.getGame_height();
        GAME_WIDTH = dataBaseConnector.getGame_width();
        this.gameViewManager = gameViewManager;
        this.brickList = brickList;
        this.waterList = waterList;

        isAlive = true;
        this.gamePane = gamePane;
        positionMatrix = collisionMatrix; //passing position matrix through reference
        ID = nextID;             //generating new ID
        nextID++;
        tankList.add(this);               //adding tank to tanks list
        this.tankList = tankList;

        collisionMatrix[spawnPosArrayX][spawnPosArrayY] = Integer.toString(ID); //saving tank position in collision matrix
        currentX = spawnPosArrayX;
        currentY = spawnPosArrayY;

        if (tankType == "RUSH")
        {

        } else if (tankType == "HUNT")
        {

        }
        tankSprite = new ImageView(tankSpriteUrl);  //loading sprite
        tankSprite.setClip(new ImageView(tankSpriteUrl));   //deleting background from sprite
        tankSprite.setLayoutX(spawnPosArrayX * 50);
        tankSprite.setLayoutY(spawnPosArrayY * 50);


        gamePane.getChildren().add(tankSprite);

        if (maxLifePoints < 1)
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

    @Override
    public void run()
    {
        while (true && !gameViewManager.isGameEnded() && isAlive)
        {
            synchronized (gameViewManager)
            {
                if (!gameViewManager.isGamePaused() && !((TankPlayer) gameViewManager.getPlayerOneTank()).getIsPaused())
                {
                    if (!gameViewManager.isMatrixAvaliable())
                    {
                        try
                        {
                            gameViewManager.wait();
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    gameViewManager.setMatrixAvaliable(false);
                    if (this.getLifePoints() > 0)
                    { //checking if tank is alive
                        this.moveTank();   //moving every tank on the map every frame
                        this.moveProjectiles();
                    } else
                    {
                        if (this.getLifePoints() == 0)
                        {
                            this.tankDestruction();
                            isAlive = false;
                            tankList.remove(this);
                        }
                    }
                    gameViewManager.setMatrixAvaliable(true);
                    gameViewManager.notifyAll();
                }
            }
            try
            {
                Thread.sleep(15);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public int getID()
    {
        return ID;
    }

    public int getCurrentX()
    {
        return currentX;
    }

    public int getCurrentY()
    {
        return currentY;
    }

    //////////////////////////////////COLLISION SYSTEM////////////////////////////////////
    boolean checkIfDownEmpty()
    {
        if (currentY < GAME_HEIGHT / BLOCK_SIZE - 1)
        {
            if (positionMatrix[currentX][currentY + 1] == null)
                return true;
            else
                return false;
        } else
            return false;
    }

    boolean checkIfUpEmpty()
    {
        if (currentY > 0)
        {
            if (positionMatrix[currentX][currentY - 1] == null)
                return true;
            else
                return false;
        } else
            return false;
    }

    boolean checkIfRightEmpty()
    {
        if (currentX < GAME_WIDTH / BLOCK_SIZE - 1)
        {
            if (positionMatrix[currentX + 1][currentY] == null)
                return true;
            else
                return false;
        } else
            return false;
    }

    boolean checkIfLeftEmpty()
    {
        if (currentX > 0)
        {
            if (positionMatrix[currentX - 1][currentY] == null)
                return true;
            else
                return false;
        } else
            return false;
    }

    //////////////////////////ANIMATIONS AND TANK MOTION////////////////////////////////////
    boolean moveTankDownOneIteration()
    {
        if (angle < 180 && angle >= 0)
        {  //checking angle of tank do select rotation direction - 3rd & 4th quarter
            if (fullSpin)
                angle += 18;
            else
                angle += 10;
        }
        if (angle > -180 && angle < 0)
        {  //movement if tank is still in game area
            if (fullSpin)
                angle -= 18;
            else
                angle -= 10;
        }
        if (angle >= -180 && angle <= 180)
        {
            Platform.runLater(() -> tankSprite.setRotate(angle));
            try
            {
                Thread.sleep(1);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        //movement if tank is still in game area and congruent block is empty
        if (tankSprite.getLayoutY() < GAME_HEIGHT - BLOCK_SIZE && allowedToMove)
        {
            Platform.runLater(()->tankSprite.setLayoutY(tankSprite.getLayoutY() + 5));
            try
            {
                Thread.sleep(1);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return true;
        } else
            return false;
    }

    boolean moveTankUpOneIteration()
    {
        if (angle >= -180 && angle < 0)
        {  //checking angle of tank do select rotation direction - 3rd & 4th quarter
            if (fullSpin)
                angle += 18;             //changing angle rotation due to fullSpin, makes sure that spin will be complete after 10 frames
            else
                angle += 10;
        }
        if (angle > 0 && angle <= 180)
        {  //1st & 2nd quarter
            if (fullSpin)
                angle -= 18;             //changing angle rotation due to fullSpin, makes sure that spin will be complete after 10 frames
            else
                angle -= 10;
        }
        Platform.runLater(()->tankSprite.setRotate(angle));
        try
        {
            Thread.sleep(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //movement if tank is still in game area and congruent block is empty
        if (tankSprite.getLayoutY() > 0 && allowedToMove)
        {
            Platform.runLater(()->tankSprite.setLayoutY(tankSprite.getLayoutY() - 5));
            try
            {
                Thread.sleep(1);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return true;
        } else
            return false;
    }

    boolean moveTankRightOneIteration()
    {
        if (angle >= -90 && angle < 90)
        { //checking angle of tank do select rotation direction - 1st & 4th quarter
            if (fullSpin)
                angle += 18;             //changing angle rotation due to fullSpin, makes sure that spin will be complete after 10 frames
            else
                angle += 10;
        }
        if (angle <= -90 || angle > 90)
        { //3rd & 4th quarter
            if (fullSpin)
                angle -= 18;
            else
                angle -= 10;
        }
        if (angle > 180)                //if passed 180 degrees point, change to minus half
            angle -= 360;
        else if (angle < -180)          //if passed -180 degrees point, change to plus half
            angle += 360;

        //movement if tank is still in game area and congruent block is empty
        Platform.runLater(()->tankSprite.setRotate(angle));
        try
        {
            Thread.sleep(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if (tankSprite.getLayoutX() < GAME_WIDTH - BLOCK_SIZE && allowedToMove)
        {
            Platform.runLater(()->tankSprite.setLayoutX(tankSprite.getLayoutX() + 5));
            try
            {
                Thread.sleep(1);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return true;
        } else
            return false;
    }

    boolean moveTankLeftOneIteration()
    {
        if (angle > -90 && angle <= 90)
        {     //checking angle of tank do select rotation direction - 1st & 4th quarter of circle
            if (fullSpin)
                angle -= 18;
            else
                angle -= 10;
        }
        if (angle < -90 || angle >= 90)
        {     //3rd & 4th quarter
            if (fullSpin)        //if tank is spinning 180 degrees for each frame it needs to spin by 18 degrees
                angle += 18;
            else
                angle += 10;
        }
        if (angle > 180)                    //if passed 180 degrees point, change to minus half
            angle -= 360;
        else if (angle < -180)              //if passed -180 degrees point, change to plus half
            angle += 360;

        Platform.runLater(()->tankSprite.setRotate(angle));

        //movement if tank is still in game area and congruent block is empty
        if (tankSprite.getLayoutX() > 0.0 && allowedToMove)
        {
            Platform.runLater(()->tankSprite.setLayoutX(tankSprite.getLayoutX() - 5));
            try
            {
                Thread.sleep(1);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return true;
        } else
            return false;
    }

    private void enemyGoLeft()
    {
        if (angle == 90)
            fullSpin = true;
        else
            fullSpin = false;

        directionOfMovement = 'L';  //giving direction to continue movement
        allowedToMove = checkIfLeftEmpty();
        if (moveTankLeftOneIteration())
        {
            positionMatrix[currentX - 1][currentY] = Integer.toString(ID);
            positionMatrix[currentX][currentY] = null;
            currentX--;
        }
        moveIterator = BLOCK_SIZE / 5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
    }

    private void enemyGoRight()
    {
        if (angle == -90)
            fullSpin = true;
        else
            fullSpin = false;

        directionOfMovement = 'R';  //giving direction to continue movement
        allowedToMove = checkIfRightEmpty();
        if (moveTankRightOneIteration())
        {
            positionMatrix[currentX + 1][currentY] = Integer.toString(ID);
            positionMatrix[currentX][currentY] = null;
            currentX++;
        }
        moveIterator = BLOCK_SIZE / 5 - 1;            //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
    }

    private void enemyGoUp()
    {
        if (angle == -180 || angle == 180)
            fullSpin = true;
        else
            fullSpin = false;

        directionOfMovement = 'U';  //giving direction to continue movement
        allowedToMove = checkIfUpEmpty();
        if (moveTankUpOneIteration())
        {
            positionMatrix[currentX][currentY - 1] = Integer.toString(ID);
            positionMatrix[currentX][currentY] = null;
            currentY--;
        }
        moveIterator = BLOCK_SIZE / 5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
    }

    private void enemyGoDown()
    {
        if (angle == 0)
            fullSpin = true;
        else
            fullSpin = false;

        directionOfMovement = 'D';  //giving direction to continue movement
        allowedToMove = checkIfDownEmpty();
        if (moveTankDownOneIteration())
        {
            positionMatrix[currentX][currentY + 1] = Integer.toString(ID);
            positionMatrix[currentX][currentY] = null;
            currentY++;
        }
        moveIterator = BLOCK_SIZE / 5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
    }

    private void startBaserushTankMovement()
    {
        int baseX = base.getCurrentX();
        int baseY = base.getCurrentY();

        if (baseY > getCurrentY() - 1 && checkIfDownEmpty())
        {
            enemyGoDown();
        }
        else if (baseY > currentY && baseX == currentX)
        {
            enemyGoDown();
        }
        else if (baseY <= getCurrentY())
        {
            if(baseX <= getCurrentX())
                enemyGoLeft();
            else
                enemyGoRight();
        }
        else if (!checkIfDownEmpty() && baseX < getCurrentX())
        {
            if (checkIfLeftEmpty())
            {
                enemyGoLeft();
            } else
            {
                enemyGoUp();
            }
        } else if (!checkIfDownEmpty() && baseX > getCurrentX())
        {
            if (checkIfRightEmpty())
            {
                enemyGoRight();
            } else
            {
                enemyGoUp();
            }
        }
    }

    private void startHunterTankMovement()
    {

        int playerX = gameViewManager.getPlayerOneTank().getCurrentX();
        int playerY = gameViewManager.getPlayerOneTank().getCurrentY();

        if (playerY > getCurrentY() && checkIfDownEmpty())
        {
            enemyGoDown();
        }
        else if(playerY < getCurrentY()){
            enemyGoUp();
        }
        else if (playerY <= getCurrentY())
        {
            if(playerX <= getCurrentX())
                enemyGoLeft();
            else
                enemyGoRight();
        }
        else if (playerX <= getCurrentX())
        {
            if (checkIfLeftEmpty())
            {
                enemyGoLeft();
            } else
            {
                enemyGoUp();
            }
        } else if (playerX >= getCurrentX())
        {
            if (checkIfRightEmpty())
            {
                enemyGoRight();
            } else
            {
                enemyGoUp();
            }
        }

    }

    private void startRandomTankMovement()
    {
        Random rand = new Random();
        int n = rand.nextInt(100);
        if (n == 0)
        {
            if (angle == 90)
                fullSpin = true;
            else
                fullSpin = false;

            directionOfMovement = 'L';  //giving direction to continue movement
            allowedToMove = checkIfLeftEmpty();
            if (moveTankLeftOneIteration())
            {
                positionMatrix[currentX - 1][currentY] = Integer.toString(ID);
                positionMatrix[currentX][currentY] = null;
                currentX--;
            }
            moveIterator = BLOCK_SIZE / 5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if (n == 1)
        {
            if (angle == -90)
                fullSpin = true;
            else
                fullSpin = false;

            directionOfMovement = 'R';  //giving direction to continue movement
            allowedToMove = checkIfRightEmpty();
            if (moveTankRightOneIteration())
            {
                positionMatrix[currentX + 1][currentY] = Integer.toString(ID);
                positionMatrix[currentX][currentY] = null;
                currentX++;
            }
            moveIterator = BLOCK_SIZE / 5 - 1;            //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if (n == 2)
        {
            if (angle == -180 || angle == 180)
                fullSpin = true;
            else
                fullSpin = false;

            directionOfMovement = 'U';  //giving direction to continue movement
            allowedToMove = checkIfUpEmpty();
            if (moveTankUpOneIteration())
            {
                positionMatrix[currentX][currentY - 1] = Integer.toString(ID);
                positionMatrix[currentX][currentY] = null;
                currentY--;
            }
            moveIterator = BLOCK_SIZE / 5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }

        if (n == 3 || n == 90)
        {
            if (angle == 0)
                fullSpin = true;
            else
                fullSpin = false;

            directionOfMovement = 'D';  //giving direction to continue movement
            allowedToMove = checkIfDownEmpty();
            if (moveTankDownOneIteration())
            {
                positionMatrix[currentX][currentY + 1] = Integer.toString(ID);
                positionMatrix[currentX][currentY] = null;
                currentY++;
            }
            moveIterator = BLOCK_SIZE / 5 - 1;           //moveIterator is set to 9, so continueTankMovement will be called in next frame instead of startTankMovement
        }
    }

    private void continueTankMovement()
    {   //function to continue movement started by pressing button, that make sure tank moves only by 50 pixels
        moveIterator--;

        if (directionOfMovement == 'L')
            moveTankLeftOneIteration();

        if (directionOfMovement == 'R')
            moveTankRightOneIteration();

        if (directionOfMovement == 'U')
            moveTankUpOneIteration();

        if (directionOfMovement == 'D')
            moveTankDownOneIteration();
    }

    public void moveTank()
    {
        if (moveIterator > 0)
            continueTankMovement();
        else if (tankType == "RUSH")
        {
            startBaserushTankMovement();
        } else if (tankType == "HUNT")
        {
            startHunterTankMovement();
        } else startRandomTankMovement();
    }


    /////////////////////////////SHOOTING//////////////////////////////////////
    boolean shoot()
    {
        if (angle == 90)
        {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix,
                    'R', listOfActiveProjectiles, tankList, base, brickList, waterList);
        } else if (angle == -90)
        {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix,
                    'L', listOfActiveProjectiles, tankList, base, brickList, waterList);
        } else if (angle == 0)
        {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix,
                    'U', listOfActiveProjectiles, tankList, base, brickList, waterList);
        } else if (angle == -180 || angle == 180)
        {
            projectile = new Projectile(gamePane, currentX, currentY, positionMatrix,
                    'D', listOfActiveProjectiles, tankList, base, brickList, waterList);
        }

        playShootSound();

        return false;
    }

    public void moveProjectiles()
    {

        if (shootDelayTimer.getCanShoot())
        {
            if (shootChance.nextInt(100) > 90)
            {     //randomize shoot chance
                shoot();
                shootDelayTimer.afterShootDelay(600);   //calling timer to prevent non stop shooting
            }
        }

        for (int x = 0; x < listOfActiveProjectiles.size(); x++)
        {
            listOfActiveProjectiles.get(x).moveProjectile();
            if (listOfActiveProjectiles.get(x).getHitConfirmed())
                listOfActiveProjectiles.remove(x);
        }
    }

    ///////////////////////////LIFE POINTS AND TANK DESTRUCTION/////////////////////
    public int getLifePoints()
    {
        return lifePoints;
    }

    void takeDamage()
    {
        lifePoints--;
        playHitSound();
        hitAnimation();
    }

    void hitAnimation()
    {
        CoulorChangerTimer timer = new CoulorChangerTimer(tankSprite);
    }

    public void tankDestruction()
    {
        positionMatrix[currentX][currentY] = null;
        lifePoints--;
        for (Projectile projectile : listOfActiveProjectiles)
        {
            projectile.hideProjectile();
        }
        playTankExplosionSound();

        destructionAnimation();
        listOfActiveProjectiles.clear();
        Platform.runLater(()->gamePane.getChildren().remove(tankSprite));
        try
        {
            Thread.sleep(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void destructionAnimation()
    {
        tankExplosion = new ImageView(EXPLOSION_SPRITE_SHEET);
        tankExplosion.setFitWidth(2 * BLOCK_SIZE);
        tankExplosion.setFitHeight(2 * BLOCK_SIZE);
        tankExplosion.setLayoutX(tankSprite.getLayoutX() - BLOCK_SIZE / 2); //placing animation
        tankExplosion.setLayoutY(tankSprite.getLayoutY() - BLOCK_SIZE / 2);
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
        Platform.runLater(()->{gamePane.getChildren().add(tankExplosion);});
        try
        {
            Thread.sleep(1);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        explosionAnimation.play();
    }


    ////////////////////////////////SOUNDS//////////////////////////////////////

    void playShootSound()
    {
        sounds = new AudioClip(this.getClass().getResource(SHOOT_SOUND).toExternalForm());
        sounds.setCycleCount(1);
        sounds.play(0.4);
    }

    void playHitSound()
    {
        sounds = new AudioClip(this.getClass().getResource(HIT_SOUND).toExternalForm());
        sounds.setCycleCount(1);
        sounds.play(0.4);
    }

    void playTankExplosionSound()
    {
        sounds = new AudioClip(this.getClass().getResource(TANK_EXPLOSION_SOUND).toExternalForm());
        sounds.setCycleCount(1);
        sounds.play(0.5);
    }
}
