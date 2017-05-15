package scv3;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.NotificationPane;

public class Main extends Application {
    public static NotificationPane notificationPane;
    public static Parent splashRoot;
    public static Parent mainRoot;

    @Override
    public void start(Stage stage) throws Exception {
        splashRoot = FXMLLoader.load(getClass().getResource("fxml/splash.fxml"));
        mainRoot = FXMLLoader.load(getClass().getResource("fxml/SCV3.fxml"));

        notificationPane = new NotificationPane(mainRoot);
        notificationPane.getStylesheets().add(getClass().getResource("assets/materialfx.css").toExternalForm());
        notificationPane.setShowFromTop(false);

        Scene scene = new Scene(splashRoot);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();
    }

    public static void message(String text, double time) {
        notificationPane.show(text);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(time * 1000),
                                                      ae -> notificationPane.hide()));
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
