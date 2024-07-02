package fi.benjamin.jpedalboard.view;

import fi.benjamin.jpedalboard.model.SliderWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class GuitarEffectPane extends Control {

    public enum Effect {
        OVERDRIVE,
        DELAY
    }

    private final Effect effect;
    private final ObservableList<SliderWrapper> sliders = FXCollections.observableArrayList();
    private final ObservableList<SliderWrapper> slidersUnmodifiable = FXCollections.unmodifiableObservableList(sliders);
    private final BooleanProperty activeProperty = new SimpleBooleanProperty();
    private final VBox container = new VBox();
    private final Label titleLabel = new Label();
    private final Button toggleButton = new Button();

    public GuitarEffectPane(Effect effect) {
        this.effect = effect;
        sliders.addListener((ListChangeListener<? super SliderWrapper>) change -> {
            container.getChildren().setAll(titleLabel);
            sliders.forEach(slider -> container.getChildren().add(slider.getSlider()));
            container.getChildren().add(toggleButton);
        });
        ImageView inActiveView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/pedal-knob-off.png"))));
        inActiveView.setFitHeight(24);
        inActiveView.setFitWidth(24);

        ImageView activeView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/pedal-knob-on.png"))));
        activeView.setFitHeight(24);
        activeView.setFitWidth(24);

        titleLabel.getStyleClass().add("effect-label");
        toggleButton.setGraphic(inActiveView);
        toggleButton.getStyleClass().add("effect-button");
        toggleButton.setOnAction(e -> {
            activeProperty.set(activeProperty.not().get());
            if (activeProperty.get()) {
                toggleButton.setGraphic(activeView);
            }
            else {
                toggleButton.setGraphic(inActiveView);
            }
        });

        container.setAlignment(Pos.CENTER);
        container.getChildren().add(titleLabel);
        container.getStyleClass().add("effect");
        container.setSpacing(16);
    }

    public ObservableList<SliderWrapper> getSliders() {
        return slidersUnmodifiable;
    }

    public ReadOnlyBooleanProperty getActiveProperty() {
        return activeProperty;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return switch (effect) {
            case OVERDRIVE -> new OverdriveSkin(this);
            case DELAY -> new DelaySkin(this);
        };
    }

    private class OverdriveSkin extends SkinBase<GuitarEffectPane> {
        private OverdriveSkin(GuitarEffectPane control) {
            super(control);

            control.titleLabel.setText("Overdrive");

            Slider gain = new Slider(0, 200, 20);
            Slider threshold = new Slider(0, 1, 0.5);
            gain.setShowTickLabels(true);
            gain.setShowTickMarks(true);
            gain.setMajorTickUnit(50.0);
            threshold.setShowTickLabels(true);
            threshold.setShowTickMarks(true);
            threshold.setMajorTickUnit(0.25);

            SliderWrapper gainSlider = new SliderWrapper(gain, SliderWrapper.Type.GAIN);
            SliderWrapper thresholdSlider = new SliderWrapper(threshold, SliderWrapper.Type.THRESHOLD);

            control.sliders.add(gainSlider);
            control.sliders.add(thresholdSlider);

            getStyleClass().add("effect-overdrive");
            getChildren().add(control.container);
        }
    }

    private class DelaySkin extends SkinBase<GuitarEffectPane> {
        private DelaySkin(GuitarEffectPane control) {
            super(control);

            control.titleLabel.setText("Delay");

            Slider delay = new Slider(0, 1, 0.5);
            Slider decay = new Slider(0, 1, 0.5);
            delay.setShowTickLabels(true);
            delay.setShowTickMarks(true);
            delay.setMajorTickUnit(0.25);
            decay.setShowTickLabels(true);
            decay.setShowTickMarks(true);
            decay.setMajorTickUnit(0.25);

            SliderWrapper delaySlider = new SliderWrapper(delay, SliderWrapper.Type.DELAY);
            SliderWrapper decaySlider = new SliderWrapper(decay, SliderWrapper.Type.DECAY);

            control.sliders.add(delaySlider);
            control.sliders.add(decaySlider);

            getStyleClass().add("effect-delay");
            getChildren().add(control.container);
        }
    }
}
