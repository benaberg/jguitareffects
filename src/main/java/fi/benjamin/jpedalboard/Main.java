package fi.benjamin.jpedalboard;

import com.synthbot.jasiohost.AsioDriver;
import fi.benjamin.jpedalboard.view.GuitarEffectPane;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private final ObservableList<GuitarEffectPane> panes = FXCollections.observableArrayList();
    private final ObservableList<String> driverList = FXCollections.observableArrayList();
    private final BooleanProperty runningProperty = new SimpleBooleanProperty();

    private AudioListener audioListener = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        AnchorPane root = new AnchorPane();
        VBox container = new VBox();
        VBox.setVgrow(container, Priority.ALWAYS);
        root.getChildren().add(container);

        AnchorPane.setTopAnchor(container, 0.0);
        AnchorPane.setRightAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setBottomAnchor(container, 0.0);

        driverList.setAll(AsioDriver.getDriverNames());

        ComboBox<String> driverBox = new ComboBox<>(driverList);

        Button startDriverButton = new Button("Start Driver");
        startDriverButton.setOnAction(e -> {
            audioListener = new AudioListener(driverBox.getSelectionModel().getSelectedItem(), AudioListener.Mode.MONO);

            runningProperty.set(true);
        });
        startDriverButton.disableProperty().bind(driverBox.getSelectionModel().selectedItemProperty().isNull().or(runningProperty));

        Button stopDriverButton = new Button("Stop Driver");
        stopDriverButton.setOnAction(e -> {
            if (audioListener != null) {
                audioListener.stop();
                runningProperty.set(false);
            }
        });
        stopDriverButton.disableProperty().bind(runningProperty.not());

        HBox topBox = new HBox(driverBox, startDriverButton, stopDriverButton);
        topBox.setSpacing(8);

        panes.addAll(new GuitarEffectPane(GuitarEffectPane.Effect.OVERDRIVE), new GuitarEffectPane(GuitarEffectPane.Effect.DELAY));

        // TODO: WTF??
        panes.forEach(pane -> pane.getSliders().addListener((ListChangeListener<? super Slider>) change -> {

        }));

        HBox effectBox = new HBox();
        effectBox.getChildren().setAll(panes);
        effectBox.setSpacing(8);

        container.getChildren().addAll(topBox, effectBox);
        container.setSpacing(8);
        container.setAlignment(Pos.TOP_CENTER);
        stage.setOnCloseRequest(close -> {
            if (audioListener != null) {
                audioListener.stop();
            }
        });

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}