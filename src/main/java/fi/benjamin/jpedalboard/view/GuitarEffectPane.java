package fi.benjamin.jpedalboard.view;

import fi.benjamin.jpedalboard.Res;
import fi.benjamin.jpedalboard.model.GuitarEffect;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class GuitarEffectPane extends Control {

    public enum EffectType {
        OVERDRIVE,
        DELAY
    }

    private final GuitarEffect guitarEffect;
    private final EffectType effectType;
    private final ObservableList<Slider> sliders = FXCollections.observableArrayList();
    private final BooleanProperty activeProperty = new SimpleBooleanProperty();
    private final ObjectProperty<Image> buttonImageProperty;
    private final ObjectProperty<Image> ledImageProperty;
    private final VBox container = new VBox();
    private final GridPane buttonPane = new GridPane();
    private final Label titleLabel = new Label();

    public GuitarEffectPane(GuitarEffect guitarEffect, EffectType effectType) {
        this.guitarEffect = guitarEffect;
        this.effectType = effectType;
        sliders.addListener((ListChangeListener<? super Slider>) change -> {
            container.getChildren().setAll(titleLabel);
            sliders.forEach(slider -> container.getChildren().add(slider));
            container.getChildren().add(buttonPane);
        });

        Image buttonOnImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/pedal-knob-on.png")));
        Image buttonOffImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/pedal-knob-off.png")));
        Image ledOnImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/pedal-led-on.png")));
        Image ledOffImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/pedal-led-off.png")));

        buttonImageProperty = new SimpleObjectProperty<>(buttonOffImage);
        ledImageProperty = new SimpleObjectProperty<>(ledOffImage);

        ImageView buttonImage = new ImageView();
        buttonImage.setFitHeight(24);
        buttonImage.setFitWidth(24);
        buttonImage.imageProperty().bind(buttonImageProperty);

        ObjectProperty<ImageView> buttonImageViewProperty = new SimpleObjectProperty<>(buttonImage);

        ImageView ledImage = new ImageView();
        ledImage.setFitHeight(8);
        ledImage.setFitWidth(8);
        ledImage.imageProperty().bind(ledImageProperty);

        titleLabel.getStyleClass().add("effect-label");

        Button toggleButton = new Button();
        toggleButton.graphicProperty().bind(buttonImageViewProperty);
        toggleButton.getStyleClass().add("effect-button");
        toggleButton.setOnAction(e -> {
            activeProperty.set(activeProperty.not().get());
            if (activeProperty.get()) {
                buttonImageProperty.set(buttonOnImage);
                ledImageProperty.set(ledOnImage);
            }
            else {
                buttonImageProperty.set(buttonOffImage);
                ledImageProperty.set(ledOffImage);
            }
        });

        buttonPane.add(toggleButton, 2, 0);
        buttonPane.add(ledImage, 3, 0);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setHgap(8);

        GridPane.setHalignment(ledImage, HPos.RIGHT);
        GridPane.setHalignment(toggleButton, HPos.CENTER);

        container.setAlignment(Pos.CENTER);
        container.getChildren().add(titleLabel);
        container.getStyleClass().add("effect");
    }

    public ReadOnlyBooleanProperty getActiveProperty() {
        return activeProperty;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return switch (effectType) {
            case OVERDRIVE -> new OverdriveSkin(this);
            case DELAY -> new DelaySkin(this);
        };
    }

    private class OverdriveSkin extends SkinBase<GuitarEffectPane> {
        private OverdriveSkin(GuitarEffectPane control) {
            super(control);

            control.titleLabel.setText(Res.getString("effect.overdrive.title"));

            Slider gainSlider = new Slider(0, 200, 20);
            Slider thresholdSlider = new Slider(0, 1, 0.5);
            gainSlider.setShowTickLabels(true);
            gainSlider.setShowTickMarks(true);
            gainSlider.setMajorTickUnit(50.0);
            thresholdSlider.setShowTickLabels(true);
            thresholdSlider.setShowTickMarks(true);
            thresholdSlider.setMajorTickUnit(0.25);

            control.sliders.add(gainSlider);
            control.sliders.add(thresholdSlider);

            gainSlider.valueProperty().addListener((observable, oldValue, newValue) -> control.guitarEffect.applySliderValues(newValue.floatValue(), -1));
            thresholdSlider.valueProperty().addListener((observable, oldValue, newValue) -> control.guitarEffect.applySliderValues(-1, newValue.floatValue()));

            getStyleClass().add("effect-overdrive");
            getChildren().add(control.container);
        }
    }

    private class DelaySkin extends SkinBase<GuitarEffectPane> {
        private DelaySkin(GuitarEffectPane control) {
            super(control);

            control.titleLabel.setText(Res.getString("effect.delay.title"));

            Slider delaySlider = new Slider(0, 1, 0.5);
            Slider decaySlider = new Slider(0, 1, 0.5);
            delaySlider.setShowTickLabels(true);
            delaySlider.setShowTickMarks(true);
            delaySlider.setMajorTickUnit(0.25);
            decaySlider.setShowTickLabels(true);
            decaySlider.setShowTickMarks(true);
            decaySlider.setMajorTickUnit(0.25);

            control.sliders.add(delaySlider);
            control.sliders.add(decaySlider);

            delaySlider.valueProperty().addListener((observable, oldValue, newValue) -> control.guitarEffect.applySliderValues(newValue.floatValue(), -1));
            decaySlider.valueProperty().addListener((observable, oldValue, newValue) -> control.guitarEffect.applySliderValues(-1, newValue.floatValue()));

            getStyleClass().add("effect-delay");
            getChildren().add(control.container);
        }
    }
}
