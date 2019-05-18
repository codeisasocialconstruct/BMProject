package View;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class MapManager
{
    //Legend:
    //0 = null;
    //3 = brick3
    //2 = brick2
    //1 = brick1
    //4 = wall
    //5 = bush
    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private ArrayList<ImageView> bushList;
    private final static int GAME_WIDTH = 250;
    private final static int GAME_HEIGHT = 250;
    private final static int BLOCK_SIZE = 50;
    private DataBaseConnector dbConnector;

    private static final String BRICK1 = "Model/Resources/MapPieces/Brick1.png";
    private static final String BRICK2 = "Model/Resources/MapPieces/Brick2.png";
    private static final String BRICK3 = "Model/Resources/MapPieces/Brick3.png";
    private static final String BUSH = "Model/Resources/MapPieces/Bush.png";
    private static final String WALL = "Model/Resources/MapPieces/Wall.png";
    private static final String BACKGROUND = "Model/Resources/MapPieces/Background.png";

    private static String[][] positionMatrix;
    private static String map_stream = "";

    public MapManager(AnchorPane gamePane, Scene gameScene, Stage gameStage)
    {
        this.gamePane = gamePane;
        this.gameScene = gameScene;
        this.gameStage = gameStage;
        positionMatrix = new String[GAME_WIDTH/BLOCK_SIZE][GAME_HEIGHT/BLOCK_SIZE];
        bushList = new ArrayList<>();
        dbConnector = new DataBaseConnector("SELECT * FROM map WHERE name = 'TEST';");
        dbConnector.getData();
        map_stream = dbConnector.getMap_stream();
    }

    public void createBackground()
    {
        Image backgroundGameImage;
        backgroundGameImage = new Image(BACKGROUND, BLOCK_SIZE, BLOCK_SIZE, false, true);
        BackgroundImage backgroundGame = new BackgroundImage(backgroundGameImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
        gamePane.setBackground(new Background(backgroundGame));
    }

    public String[][] createPositionMatrix()
    {
        int counter = 0;
        char[] tmp = map_stream.toCharArray();

        for(int i=0; i<GAME_WIDTH/BLOCK_SIZE; i++ )
            for (int j=0; j<GAME_HEIGHT/BLOCK_SIZE; j++ )
            {
                switch (tmp[counter])
                {
                    case '3':
                    {
                        positionMatrix[j][i] = "Brick3";
                        break;
                    }
                    case '2':
                    {
                        positionMatrix[j][i] = "Brick2";
                        break;
                    }
                    case '1':
                    {
                        positionMatrix[j][i] = "Brick1";
                        break;
                    }
                    case '4':
                    {
                        positionMatrix[j][i] = "Wall";
                        break;
                    }

                }
                counter++;
            }
        return positionMatrix;
    }

    public void createMap()
    {
        int counter = 0;
        char[] tmp = map_stream.toCharArray();
        String picture;

        for(int i=0; i<GAME_WIDTH/BLOCK_SIZE; i++ )
            for (int j=0; j<GAME_HEIGHT/BLOCK_SIZE; j++ )
            {
                if(tmp[counter] != '0')
                {
                    switch (tmp[counter])
                    {
                        case '3':
                        {
                            picture = BRICK3;
                            break;
                        }
                        case '2':
                        {
                            picture = BRICK2;
                            break;
                        }
                        case '1':
                        {
                            picture = BRICK1;
                            break;
                        }
                        case '4':
                        {
                            picture = WALL;
                            break;
                        }
                        case '5':
                        {
                            picture = BUSH;
                            break;
                        }
                        default:
                        {
                            picture = BUSH;
                            break;
                        }
                    }

                    ImageView imageView = new ImageView(new Image(picture, BLOCK_SIZE, BLOCK_SIZE, false, true));
                    imageView.setLayoutX(j * BLOCK_SIZE);
                    imageView.setLayoutY(i * BLOCK_SIZE);
                    if(tmp[counter] == '5')
                        bushList.add(imageView);
                    gamePane.getChildren().add(imageView);
                }
                counter++;
            }
    }

    public void bushToFront()
    {
        for(ImageView i : bushList)
        {
            i.toFront();
        }
    }
}
