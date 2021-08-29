package me.frosty.application;

import spotisaber.api.SpotiSaberMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.Getter;
import me.frosty.application.javascript.JavaScriptFeedback;
import me.frosty.docsplus.DocsBootstrap;
import me.frosty.docsplus.annotation.Note;
import me.frosty.docsplus.annotation.Reference;
import netscape.javascript.JSObject;

@Getter // By frosty.
public class ApplicationBootstrap extends Application {

    @Getter private static ApplicationBootstrap instance;

    private SpotiSaberMain spotiSaberMain;

    private JavaScriptFeedback feedback;

    private WebView webView;


    @Override @Note("Creates the application bootstrap and initializes jerrys thing")
    @Reference(SpotiSaberMain.class)
    public void start(Stage stage) {
        instance = this;

        DocsBootstrap.setup("SPOTISABER");

        this.spotiSaberMain = new SpotiSaberMain();

        this.webView = new WebView();

        this.feedback = new JavaScriptFeedback(this);

        final VBox layout = new VBox();

        layout.getChildren().add(this.webView);

        stage.setScene(new Scene(layout));

        stage.getIcons().add(new Image("logo.png"));
        stage.setTitle("Spotisaber");

        stage.setResizable(false);

        stage.setWidth(1200);
        stage.setHeight(638);

        this.webView.setContextMenuEnabled(false);

        Platform.runLater(() -> {

            this.webView.getEngine().load(ClassLoader.getSystemResource("index.html").toExternalForm());

            this.webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {

                if (newValue == Worker.State.SUCCEEDED) {

                    ((JSObject) this.webView.getEngine().executeScript("window")).setMember("feedback", this.feedback);

                }
            });

        });

        stage.show(); // Show the stage.


    }

    public void terminate() {
        Runtime.getRuntime().exit(0);
    }
}
