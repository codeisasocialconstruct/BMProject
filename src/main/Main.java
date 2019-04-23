package main;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application
{

    private final Image IMAGE = new Image(getClass().getResourceAsStream("1.png"));

    private static final int COLUMNS = 4;
    private static final int COUNT = 8;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;
    private static final int WIDTH = 512;
    private static final int HEIGHT = 256;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ViewManager manager = new ViewManager();

        primaryStage = manager.getMainStage();
        primaryStage.show();
        ///////////////Cat animation/////////////////
        /*primaryStage.setTitle("The Horse in Motion");

        final ImageView imageView = new ImageView(IMAGE);
        imageView.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT));

        final Animation animation = new SpriteAnimation(
                imageView,
                Duration.millis(500),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                WIDTH, HEIGHT
        );
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();

        primaryStage.setScene(new Scene(new Group(imageView)));
        primaryStage.show();*/
    }


    public static void main(String[] args)
    {
        launch(args);
    }
}
