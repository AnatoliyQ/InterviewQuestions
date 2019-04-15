package ru.anatoliy.electrolux;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.anatoliy.electrolux.presenter.Presenter;
import ru.anatoliy.electrolux.service.Service;
import ru.anatoliy.electrolux.view.ApplicationView;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        ApplicationView root = new ApplicationView();
        root.setPresenter(new Presenter(root, new Service()));
        root.build();

        final Scene scene = new Scene(root, 410, 400);
        scene.getStylesheets().add("css/style.css");

        primaryStage.setTitle("Log Monitor");
        primaryStage.getIcons().add(new Image(App.class.getResourceAsStream("/images/Electrolux_logo.png")));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
