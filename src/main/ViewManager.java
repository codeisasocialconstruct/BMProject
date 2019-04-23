package main;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViewManager {

    private final int HEIGHT = 600;
    private final int WIDTH = 800;
    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    public ViewManager() {
        mainPane = new AnchorPane();
        mainScene = new Scene(mainPane, WIDTH, HEIGHT);
        mainStage = new Stage();

        mainStage.setScene(mainScene);
    }

    public Stage getMainStage() {
        return mainStage;
    }
}
