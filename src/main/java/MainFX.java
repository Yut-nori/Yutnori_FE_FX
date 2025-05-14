import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello JavaFX");

        Label label = new Label("JavaFX 실행 성공!");
        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
