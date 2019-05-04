package Model.Tanks;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class Projectile {
    private String[][] positionMatrix;
    private char directionOfMovement;
    private ImageView projectileSprite;
    private int currentX;
    private int currentY;
    private int moveIterator;

    private final static String PROJECTILE_SPRITE = "Model/Resources/Projectiles/shotThin.png";

    public Projectile(AnchorPane gamePane, int spawnPosArrayX, int spawnPosArrayY, String[][] positionMatrix, char directionOfMovement, List<Projectile> projectileList) {
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
    }

    public void moveProjectile() {
        if(moveIterator==0)
            startMovement();
        else
            continueMovement();
    }

    private void startMovement() {

    }

    private void continueMovement() {

    }
}
