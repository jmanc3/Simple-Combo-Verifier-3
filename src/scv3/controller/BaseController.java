package scv3.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import org.controlsfx.control.TaskProgressView;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.textfield.CustomTextField;
import scv3.Main;
import scv3.utils.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseController implements Initializable {
    @FXML
    public CustomTextField passMinField;
    @FXML
    public ToggleButton passMinToggle;
    @FXML
    public ToggleSwitch passMinLengthToggle;
    @FXML
    public CustomTextField passMaxField;
    @FXML
    public ToggleButton passCapToggle;
    @FXML
    public ToggleButton passNumToggle;
    @FXML
    public ToggleButton passSpecialToggle;
    @FXML
    public CustomTextField passCustomField;
    @FXML
    public ToggleSwitch passCustomToggle;
    @FXML
    public ToggleSwitch userMinLengthToggle;
    @FXML
    public CustomTextField userMaxField;
    @FXML
    public ToggleSwitch userMaxLengthToggle;
    @FXML
    public ToggleButton userMinToggle;
    @FXML
    public ToggleButton userCapToggle;
    @FXML
    public ToggleButton userNumToggle;
    @FXML
    public ToggleButton userSpecialToggle;
    @FXML
    public ToggleSwitch userCustomToggle;
    @FXML
    public CustomTextField userCustomField;
    @FXML
    public ToggleSwitch passMaxLengthToggle;
    @FXML
    public CustomTextField userMinField;
    @FXML
    public TaskProgressView taskView;
    @FXML
    public ChoiceBox threadAmount;
    @FXML
    public VBox vbox;

    protected List<File> selectedFiles;

    protected ExecutorService executorService = Executors.newCachedThreadPool();

    protected File tempFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utils.RestrictNumbersOnly(passMaxField);
        Utils.RestrictNumbersOnly(passMinField);
        Utils.RestrictNumbersOnly(userMaxField);
        Utils.RestrictNumbersOnly(userMinField);
        Utils.attach(passMaxField, passMaxLengthToggle);
        Utils.attach(passMinField, passMinLengthToggle);
        Utils.attach(userMaxField, userMaxLengthToggle);
        Utils.attach(userMinField, userMinLengthToggle);
        Utils.attach(passCustomField, passCustomToggle);
        Utils.attach(userCustomField, userCustomToggle);

        selectedFiles = new ArrayList<>();

        String tempLocation = System.getProperty("java.io.tmpdir");
        tempFile = new File(tempLocation + "tempVerified.txt");

        taskView.setGraphicFactory(task -> {
            try {
                return new ImageView(String.valueOf(new File("src/scv3/assets/text-icon.png").toURI().toURL()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        });

        threadAmount.setItems(FXCollections.observableArrayList(
                "1 Thread", "2 Max Threads", "4 Max Threads", "6 Max Threads", "8 Max Threads", "16 Max Threads", "32 Max Threads")
        );

        threadAmount.setValue("4 Max Threads");

        //drag and drop
        vbox.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.LINK);
            } else {
                event.consume();
            }
        });
        vbox.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String fileName = null;
                selectedFiles.clear();
                StringBuilder sb = new StringBuilder();
                for (File file:db.getFiles()) {
                    fileName = file.getName();
                    if (fileName.contains(".txt")) {
                        selectedFiles.add(file);
                        sb.append(fileName + ", ");
                    }
                }
                sb.replace(sb.length() - 2, sb.length(), "");
                Main.message("Selected " + sb.toString(), 1.5);
            }
            event.setDropCompleted(success);
            event.consume();
        });

    }

    public abstract void open();

    public abstract void verify() throws InterruptedException;

    public abstract void save();

    public abstract void saveAs();

    public void reset() {
        passMinField.clear();
        passMinToggle.setSelected(false);
        passMinLengthToggle.setSelected(false);
        passMaxField.clear();
        passCapToggle.setSelected(false);
        passNumToggle.setSelected(false);
        passSpecialToggle.setSelected(false);
        passCustomField.clear();
        passCustomToggle.setSelected(false);
        userMinLengthToggle.setSelected(false);
        userMaxField.clear();
        userMaxLengthToggle.setSelected(false);
        userMinToggle.setSelected(false);
        userCapToggle.setSelected(false);
        userNumToggle.setSelected(false);
        userSpecialToggle.setSelected(false);
        userCustomToggle.setSelected(false);
        userCustomField.clear();
        passMaxLengthToggle.setSelected(false);
        userMinField.clear();
    }
}
