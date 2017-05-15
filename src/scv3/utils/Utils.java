package scv3.utils;

import javafx.scene.control.TextField;
import org.controlsfx.control.ToggleSwitch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Utils {

    public static void RestrictNumbersOnly(TextField tf) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("|[-\\+]?|[-\\+]?\\d+\\.?|[-\\+]?\\d+\\.?\\d+")) {
                tf.setText(oldValue);
            }
        });
    }

    public static void attach(TextField tf, ToggleSwitch toggleSwitch) {
        tf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (tf.getText().length() > 0) {
                toggleSwitch.setSelected(true);
            } else {
                toggleSwitch.setSelected(false);
            }
        });
    }

    public static int countLines(File aFile) throws IOException {
        FileInputStream stream = new FileInputStream(aFile);
        byte[] buffer = new byte[8192];
        int count = 0;
        int n;
        while ((n = stream.read(buffer)) > 0) {
            for (int i = 0; i < n; i++) {
                if (buffer[i] == '\n') count++;
            }
        }
        stream.close();
        return count;
    }

}
