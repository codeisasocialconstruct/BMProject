package Model.Tanks;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class Projectile {
    private AnchorPane gamePane;
    private String[][] positionMatrix;
    private char directionOfMovement;
    private ImageView projectileSprite;
    private int currentX;
    private int currentY;
    private int moveIterator;
    private boolean hitConfirmed;

    private final static String PROJECTILE_SPRITE = "Model/Resources/Projectiles/shotThin.png";

    public Projectile(AnchorPane gamePane, int spawnPosArrayX, int spawnPosArrayY, String[][] positionMatrix, char directionOfMovement, List<Projectile> projectileList) {
        this.gamePane = gamePane;
        this.positionMatrix = positionMatrix;
        this.directionOfMovement = directionOfMovement;
        currentX = spawnPosArrayX;
        currentY = spawnPosArrayY;

        projectileSprite = new ImageView(PROJECTILE_SPRITE);  //loading sprite
        if(directionOfMovement=='L') {
            projectileSprite.setRotate(-90);
            projectileSprite.setLayoutY(spawnPosArrayY*50 + 12);
            projectileSprite.setLayoutX(spawnPosArrayX*50 - 5);
        }
        else if(directionOfMovement=='R') {
            projectileSprite.setRotate(90);
            projectileSprite.setLayoutY(spawnPosArrayY*50 + 12);
            projectileSprite.setLayoutX(spawnPosArrayX*50 + 50);
        }
        else if(directionOfMovement=='D') {
            projectileSprite.setRotate(-180);
            projectileSprite.setLayoutX(spawnPosArrayX*50 + 21);
            projectileSprite.setLayoutY(spawnPosArrayY*50 + 35);
        }
        else {
            projectileSprite.setLayoutX(spawnPosArrayX*50 + 21);
            projectileSprite.setLayoutY(spawnPosArrayY*50 - 10);
        }
        projectileList.add(this);
        gamePane.getChildren().add(projectileSprite);

        moveIterator=0;
        hitConfirmed = false;
    }

    public boolean getHitConfirmed() {return hitConfirmed;}

    protected boolean checkIfDownEmpty() {
        if (currentY < Tank.GAME_HEIGHT/Tank.BLOCK_SIZE-1) {
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
        if (currentX < Tank.GAME_WIDTH/Tank.BLOCK_SIZE-1) {
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

    public void moveProjectile() {
        if(moveIterator==0)
            startMovement();
        else
            continueMovement();
    }

    private void startMovement() {
        if(directionOfMovement=='R') {
            projectileSprite.setLayoutX(projectileSprite.getLayoutX() + 10);

            if(!checkIfRightEmpty()) {
                hitConfirmed = true;
                gamePane.getChildren().remove(projectileSprite);
            }
            else
                currentX++;
        }
        else if(directionOfMovement=='L') {
            projectileSprite.setLayoutX(projectileSprite.getLayoutX() - 10);

            if(!checkIfLeftEmpty()) {
                hitConfirmed = true;
                gamePane.getChildren().remove(projectileSprite);
            }
            else
                currentX--;
        }
        else if(directionOfMovement=='U') {
            projectileSprite.setLayoutY(projectileSprite.getLayoutY() - 10);

            if(!checkIfUpEmpty()) {
                hitConfirmed = true;
                gamePane.getChildren().remove(projectileSprite);
            }
            else
                currentY--;
        }
        else if(directionOfMovement=='D') {
            projectileSprite.setLayoutY(projectileSprite.getLayoutY() + 10);

            if(!checkIfDownEmpty()) {
                hitConfirmed = true;
                gamePane.getChildren().remove(projectileSprite);
            }
            else
                currentY++;
        }
        moveIterator = Tank.BLOCK_SIZE/10 - 1;
    }

    private void continueMovement() {
        moveIterator--;
        if(directionOfMovement=='R') {
            projectileSprite.setLayoutX(projectileSprite.getLayoutX() + 10);
        }
        else if(directionOfMovement=='L') {
            projectileSprite.setLayoutX(projectileSprite.getLayoutX() - 10);
        }
        else if(directionOfMovement=='U') {
            projectileSprite.setLayoutY(projectileSprite.getLayoutY() - 10);
        }
        else if(directionOfMovement=='D') {
            projectileSprite.setLayoutY(projectileSprite.getLayoutY() + 10);
        }

    }
}
