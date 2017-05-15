package scv3.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import scv3.Main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML
    public Pane pane;

    private SplashScreen splashScreen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        splashScreen = new SplashScreen();

        splashScreen.start();
    }

    public void click() {
        splashScreen.interrupt();
    }

    class SplashScreen extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1875);
            } catch (InterruptedException ignored) {

            }

            Platform.runLater(() -> {
                Stage stage = new Stage();

                stage.getIcons().addAll(
                        new Image(Main.class.getResourceAsStream("assets/search0.png")),
                        new Image(Main.class.getResourceAsStream("assets/search1.png")),
                        new Image(Main.class.getResourceAsStream("assets/search2.png")),
                        new Image(Main.class.getResourceAsStream("assets/search3.png")),
                        new Image(Main.class.getResourceAsStream("assets/search4.png"))
                );

                stage.setTitle("SCV3: jmanc3");
                Scene scene = new Scene(Main.notificationPane);
                stage.setScene(scene);
                stage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent window) {

                    }
                });
                stage.show();


                pane.getScene().getWindow().hide();
            });
        }

    }
}
