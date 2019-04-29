package View;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class GameViewManager {
    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private Stage menuStage;
    private ImageView playerOneTank;

    private final static int GAME_WIDTH = 800;
    private final static int GAME_HEIGHT = 600;

    public GameViewManager() {
        initializeStage();
        createKeyListeners();
    }

    private void initializeStage() {
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
    }

    //Creating Listeners to control players tank
    private void createKeyListeners() {
        gameScene.setOnKeyPressed(event -> {  //lambda function to handle event
            if(event.getCode() == KeyCode.LEFT) {

            }
            else if (event.getCode() == KeyCode.RIGHT) {

            }
            else if (event.getCode() == KeyCode.UP) {

            }
            else if (event.getCode() == KeyCode.DOWN) {

            }
            else if (event.getCode() == KeyCode.SPACE) {

            }

        });

        gameScene.setOnKeyReleased( event -> {
            if(event.getCode() == KeyCode.LEFT) {

            }
            else if (event.getCode() == KeyCode.RIGHT) {

            }
            else if (event.getCode() == KeyCode.UP) {

            }
            else if (event.getCode() == KeyCode.DOWN) {

            }
            else if (event.getCode() == KeyCode.SPACE) {

            }
        });
    }

    //showing game window
    public void createGame(Stage menuStage, boolean twoPlayersMode) {
        this.menuStage = menuStage;
        this.menuStage.hide();
        createTank();
        gameStage.show();
    }

    private void createTank() {
        playerOneTank = new ImageView("View/resources/tank_red.png");
        playerOneTank.setLayoutX(GAME_WIDTH/2);
        playerOneTank.setLayoutY(GAME_HEIGHT-50);

        gamePane.getChildren().add(playerOneTank);
    }

}
