import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import util.GameManager;
import util.GameState;
import util.ScreenManager;


public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Yutnori -Team 13");

        GameManager gm = new GameManager(new GameState());
        ScreenManager sm = new ScreenManager(primaryStage, gm);
        gm.setScreenManager(sm);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
