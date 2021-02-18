package sample;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Timer;

public class RunSceneController1 extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("iRecorder");
        Parent root = FXMLLoader.load(getClass().getResource("/window.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (Holder.map.containsKey("timer")) {
                    Timer timer = Holder.map.get("timer");
                    timer.cancel();
                    System.out.print("【事件】监听到窗口关闭");
                }
            }
        });
    }
    public static void main(String[] args) {
        launch(args);
    }
}
