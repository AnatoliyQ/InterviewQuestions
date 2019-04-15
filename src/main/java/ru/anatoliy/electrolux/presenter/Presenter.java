package ru.anatoliy.electrolux.presenter;

import java.io.File;
import java.util.function.Consumer;
import javafx.stage.FileChooser;
import ru.anatoliy.electrolux.service.Service;
import ru.anatoliy.electrolux.view.ApplicationView;

public class Presenter {

    private ApplicationView applicationView;
    private Service service;

    public Presenter(ApplicationView applicationView, Service service) {
        this.service = service;
        this.applicationView = applicationView;
    }

    public void setLogFilePath() {
        File logFile = getCustomFileChooser("log file (*.txt)", "*.txt", "Select log file");
        if (!(logFile == null)) {
            String logPath = logFile.getPath();
            service.setLogFilePath(logPath);
            applicationView.changeButtonParseEnable(false);
        }
    }

    public void parseFile(Consumer<Integer> consumer) {
        Thread parserThread = new Thread(() -> {
            service.findErrors(consumer);
        });
        parserThread.start();
    }

    private File getCustomFileChooser(String description, String extensions, String title) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                description, extensions);

        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle(title);

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Documents"));
        return fileChooser.showOpenDialog(applicationView.getScene().getWindow());
    }
}
