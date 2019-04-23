package View;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import Model.NavigationButton;

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
        CreateButtons();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private void CreateButtons() {
        NavigationButton button = new NavigationButton("Click!");
        button.setLayoutX(100);
        button.setLayoutY(100);
        mainPane.getChildren().add(button);
    }
}
