package scv3.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.controlsfx.control.action.Action;
import scv3.Main;
import scv3.utils.AbstractBuffer;
import scv3.utils.Combo;
import scv3.utils.Messages;
import scv3.utils.Verifier;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller extends BaseController {
    private ArrayList<Task> tasks;
    private BufferedWriter writer;

    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File(s)");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt", "*.TXT", "*.Txt");
        fileChooser.getExtensionFilters().add(extFilter);

        if (!selectedFiles.isEmpty()) {
            String file = selectedFiles.get(0).getAbsolutePath();
            fileChooser.setInitialDirectory(new File(file.substring(0, file.lastIndexOf("\\") + 1)));
        }

        List<File> files = fileChooser.showOpenMultipleDialog(null);

        if (files != null) {
            selectedFiles = files;
            StringBuilder sb = new StringBuilder();
            for (File file : selectedFiles) {
                if (file.equals(selectedFiles.get(selectedFiles.size() - 1))) {
                    sb.append(file.getName());
                } else {
                    sb.append(file.getName()).append(", ");
                }
            }
            Main.message("Selected: " + sb, 1.5);
        }
    }

    public void verify() throws InterruptedException {
        //check errors
        if (!taskView.getTasks().isEmpty()) {
            Main.message(Messages.TASKNOTDONE, 1.5);
            return;
        }
        if (selectedFiles == null || selectedFiles.size() == 0 || selectedFiles.isEmpty()) {
            Main.message(Messages.NOFILESSELECTED, 1.5);
            return;
        }

        tasks = new ArrayList<>();

        for (int i = 0; i < selectedFiles.size(); i++) {
            Task task = multiTask(selectedFiles.get(i), i, this);
            taskView.getTasks().add(task);
            tasks.add(task);
        }

        try {
            writer = new BufferedWriter(new FileWriter(tempFile.getAbsolutePath(), false));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String threadAmountStr = threadAmount.getValue().toString();

        Task allTasks = allTasks(Integer.parseInt(threadAmountStr.substring(0, threadAmountStr.indexOf(" "))));

        taskView.getTasks().add(0, allTasks);


        allTasks.setOnCancelled(event -> {
            for (Task task : tasks) {
                task.cancel();
            }
            taskView.getTasks().clear();
        });

        executorService.submit(allTasks);
    }

    public void save() {
        if (!taskView.getTasks().isEmpty()) {
            Main.message(Messages.CANTSAVE, 1.5);
            return;
        }
        if (selectedFiles == null || selectedFiles.size() == 0 || selectedFiles.isEmpty()) {
            Main.message(Messages.NOTHINGTOOVERWRITE, 1.5);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Confirm");
        alert.setContentText("Are you sure you wish to overwrite: " + selectedFiles.get(0).getAbsolutePath());

        ButtonType buttonTypeOne = new ButtonType("Continue", ButtonBar.ButtonData.LEFT);
        ButtonType buttonTypeTwo = new ButtonType("Save As", ButtonBar.ButtonData.LEFT);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            doSaveWork(selectedFiles.get(0));
        } else if (result.get() == buttonTypeTwo) {
            saveAs();
        }
    }

    public void saveAs() {
        if (!taskView.getTasks().isEmpty()) {
            Main.message(Messages.CANTSAVE, 1.5);
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt", "*.TXT", "*.Txt");
        fileChooser.getExtensionFilters().add(extFilter);

        if (!selectedFiles.isEmpty()) {
            String file = selectedFiles.get(0).getAbsolutePath();
            fileChooser.setInitialDirectory(new File(file.substring(0, file.lastIndexOf("\\") + 1)));
            fileChooser.setInitialFileName(selectedFiles.get(0).getName().replaceAll(".txt", "-SCM.txt"));
        }

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            doSaveWork(file);
        } else {
            Main.message("Nothing Done...", 1.5);
        }
    }

    private void doSaveWork(File targetFile) {
        Task doSaveWork = new Task() {
            @Override
            protected Object call() throws Exception {
                updateTitle("Saving Task");
                InputStream inStream = null;
                OutputStream outStream = null;

                try {
                    inStream = new FileInputStream(tempFile);
                    outStream = new FileOutputStream(targetFile);

                    long fileLength = tempFile.length();
                    long counter = 0L;

                    byte[] buffer = new byte[1024];

                    int length;

                    while ((length = inStream.read(buffer)) > 0) {
                        if (isCancelled()) break;

                        counter += (long) length;
                        updateProgress(counter, fileLength);
                        outStream.write(buffer, 0, length);
                    }

                    Platform.runLater(() -> {
                        Main.notificationPane.getActions().setAll(new Action("Open", ae -> {
                            try {
                                Runtime.getRuntime().exec("explorer.exe /select," + targetFile.getAbsolutePath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Main.notificationPane.hide();
                        }));

                        Main.message("Done. Show File in Explorer?", 5);
                    });
                } catch (IOException e) {
                    Main.message("Something With The Files Went Wrong ¯\\_(ツ)_/¯", 5);
                    e.printStackTrace();
                } finally {
                    if (inStream != null) {
                        inStream.close();
                    }
                    if (outStream != null) {
                        outStream.close();
                    }
                }

                return null;
            }
        };

        taskView.getTasks().add(doSaveWork);
        executorService.submit(doSaveWork);
    }

    private synchronized void appendContents(String sContent) {
        try {
            writer.write(sContent + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Task allTasks(final int s) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                try {

                    updateProgress(0, 1);
                    updateMessage("0/" + tasks.size());
                    updateTitle("All Tasks");
                    AtomicInteger doneTasks = new AtomicInteger(0);

                    while (!tasks.stream().allMatch(FutureTask::isDone)) {
                        Platform.runLater(() -> {
                            AtomicInteger runningTasks = new AtomicInteger(0);

                            for (Task task : tasks) {
                                if (task.isRunning()) {
                                    runningTasks.addAndGet(1);
                                }
                            }
                            for (int i = 0; i < tasks.size(); i++) {
                                Task task = tasks.get(i);
                                if (!task.isRunning() && !task.isDone() && !task.isCancelled()) {
                                    if (runningTasks.get() < s) {
                                        runningTasks.addAndGet(1);
                                        executorService.submit(task);
                                        task.setOnSucceeded(event -> {
                                            doneTasks.addAndGet(1);
                                            updateProgress(doneTasks.get(), tasks.size());
                                            updateMessage(doneTasks.get() + "/" + tasks.size());
                                        });
                                        task.setOnCancelled(event -> {
                                            doneTasks.addAndGet(1);
                                            updateProgress(doneTasks.get(), tasks.size());
                                            updateMessage(doneTasks.get() + "/" + tasks.size());
                                        });
                                        task.setOnFailed(event -> {
                                            doneTasks.addAndGet(1);
                                            updateProgress(doneTasks.get(), tasks.size());
                                            updateMessage(doneTasks.get() + "/" + tasks.size());
                                        });
                                    }
                                }
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {

                        }
                    }
                } finally {
                    writer.close();
                }
                return null;
            }
        };
    }

    private Task multiTask(File file, int i, Controller controller) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                updateProgress(0, 1);
                updateMessage(file.getName());
                updateTitle("Task: " + i);

                AbstractBuffer buffer = new AbstractBuffer(file);
                Combo combo = new Combo();
                Verifier verifier = new Verifier(controller);

                for (int i = 0; i < buffer.getLimit(); i++) {
                    if (isCancelled()) break;

                    if (i % buffer.getDivider() == 0) {
                        updateProgress(i, buffer.getLimit());
                    }

                    combo.append(buffer.getNextChar());

                    if (combo.isFound()) {
                        if (verifier.passWrongLength(combo.passLength))
                            continue;
                        if (verifier.userWrongLength(combo.userLength))
                            continue;
                        if (verifier.passFailToggles(combo))
                            continue;
                        if (verifier.userFailToggles(combo))
                            continue;

                        String comboString = String.copyValueOf(combo.array, 0, combo.length);

                        if (verifier.passFailCustom(combo, comboString))
                            continue;
                        if (verifier.userFailCustom(combo, comboString))
                            continue;

                        appendContents(String.valueOf(comboString));
                    }
                }

                buffer.close();
                return null;
            }
        };
    }
}
