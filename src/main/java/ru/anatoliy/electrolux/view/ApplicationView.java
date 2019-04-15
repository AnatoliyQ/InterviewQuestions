package ru.anatoliy.electrolux.view;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Setter;
import ru.anatoliy.electrolux.constants.Globals;
import ru.anatoliy.electrolux.presenter.Presenter;

public class ApplicationView extends BorderPane {
    @Setter
    private Presenter presenter;

    private Button bParseLog;

    public void build() {
        Button bSetFilePath = new Button("Set Path");
        bSetFilePath.setTooltip(new Tooltip("Choose log file to find errors"));
        bSetFilePath.setOnAction(event -> {
            presenter.setLogFilePath();
        });

        bParseLog = new Button("Parse");
        bParseLog.setTooltip(new Tooltip("Start finding errors"));
        bParseLog.setDisable(true);
        bParseLog.setOnAction(event -> {
            changeButtonParseEnable(true);
            presenter.parseFile(integer -> {
                Platform.runLater(() -> {
                    showResultDialog(integer);
                    changeButtonParseEnable(false);
                });
            });
        });

        HBox center = new HBox(bSetFilePath, bParseLog);
        center.getStyleClass().add("control-pane");

        Image centerLogo = new Image((getClass().getResourceAsStream("/images/main_jpg.jpg")));
        ImageView imageView = new ImageView(centerLogo);
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);

        VBox temp = new VBox();
        temp.getChildren().add(imageView);

        setCenter(new Pane(temp));

        setBottom(center);

        getStyleClass().add("app-view");
    }

    public void changeButtonParseEnable(Boolean state) {
        bParseLog.setDisable(state);
    }

    private void showResultDialog(int errorCounter) {
        Alert resultDialog = new Alert(AlertType.INFORMATION);
        resultDialog.setTitle("Result Log Monitor");
        resultDialog.setHeaderText("Log file has been processed");
        resultDialog.setContentText(String.format("Found %d errors. Report at - %s", errorCounter, Globals.RESULT_FILE_PATH));
        Stage stage = (Stage) resultDialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/images/Electrolux_logo.png"));

        resultDialog.getDialogPane().getStylesheets().add(getClass().getResource(Globals.PATH_CSS_DIALOG).toExternalForm());

        resultDialog.showAndWait();
    }
}
