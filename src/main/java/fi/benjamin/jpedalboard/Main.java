package fi.benjamin.jpedalboard;

import com.synthbot.jasiohost.AsioDriver;
import fi.benjamin.jpedalboard.io.AudioListener;
import fi.benjamin.jpedalboard.model.DelayEffect;
import fi.benjamin.jpedalboard.model.GuitarEffect;
import fi.benjamin.jpedalboard.model.OverdriveEffect;
import fi.benjamin.jpedalboard.model.SliderWrapper;
import fi.benjamin.jpedalboard.view.GuitarEffectPane;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    private final ObservableList<GuitarEffectPane> panes = FXCollections.observableArrayList();
    private final ObservableList<String> driverList = FXCollections.observableArrayList();
    private final ObservableMap<GuitarEffectPane, GuitarEffect> effectMap = FXCollections.observableHashMap();
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

        Button startDriverButton = new Button(Res.getString("button.start.driver"));
        startDriverButton.setOnAction(e -> {
            audioListener = new AudioListener(driverBox.getSelectionModel().getSelectedItem(), AudioListener.Mode.MONO, effectMap.values().stream().toList());
            runningProperty.set(true);
        });
        startDriverButton.disableProperty().bind(driverBox.getSelectionModel().selectedItemProperty().isNull().or(runningProperty));

        Button stopDriverButton = new Button(Res.getString("button.stop.driver"));
        stopDriverButton.setOnAction(e -> {
            if (audioListener != null) {
                audioListener.stop();
                runningProperty.set(false);
            }
        });
        stopDriverButton.disableProperty().bind(runningProperty.not());

        HBox topBox = new HBox(driverBox, startDriverButton, stopDriverButton);
        topBox.setSpacing(4);
        topBox.setPadding(new Insets(8));

        HBox effectBox = new HBox();
        VBox.setVgrow(effectBox, Priority.ALWAYS);
        effectBox.setSpacing(20);
        effectBox.setAlignment(Pos.CENTER);

        effectMap.addListener((MapChangeListener<? super GuitarEffectPane, ? super GuitarEffect>) change -> {
            panes.setAll(effectMap.keySet());
            panes.forEach(pane -> {
                pane.getSliders().addListener((ListChangeListener<? super SliderWrapper>) sliderChange -> {
                    while (sliderChange.next()) {
                        for (SliderWrapper slider : sliderChange.getAddedSubList()) {
                            slider.getSlider().valueProperty().addListener(valueChange -> {
                                switch (slider.getType()) {
                                    case GAIN, DELAY -> effectMap.get(pane).applySliderValues((float) slider.getSlider().getValue(), -1);
                                    case THRESHOLD, DECAY -> effectMap.get(pane).applySliderValues(-1, (float) slider.getSlider().getValue());
                                }
                            });
                        }
                    }
                });
                pane.getActiveProperty().addListener((ChangeListener<? super Boolean>) (obs, old, neo) ->
                        effectMap.get(pane).setActive(neo));
            });
            effectBox.getChildren().setAll(panes);
        });

        container.getChildren().addAll(topBox, effectBox);
        container.setSpacing(8);
        container.setAlignment(Pos.TOP_CENTER);
        stage.setOnCloseRequest(close -> {
            if (audioListener != null) {
                audioListener.stop();
            }
        });

        initEffects();

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("view/main.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle(Res.getString("application.title"));
        stage.show();
    }

    private void initEffects() {
        GuitarEffect overdriveEffect = new OverdriveEffect();
        GuitarEffectPane overdrivePane = new GuitarEffectPane(GuitarEffectPane.Effect.OVERDRIVE);
        VBox.setVgrow(overdrivePane, Priority.NEVER);

        GuitarEffect delayEffect = new DelayEffect();
        GuitarEffectPane delayPane = new GuitarEffectPane(GuitarEffectPane.Effect.DELAY);
        VBox.setVgrow(delayPane, Priority.NEVER);

        effectMap.put(overdrivePane, overdriveEffect);
        effectMap.put(delayPane, delayEffect);
    }
}