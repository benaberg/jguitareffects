package fi.benjamin.jpedalboard.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class GuitarEffectPane extends Control {

    public enum Effect {
        OVERDRIVE,
        DELAY
    }

    private final Effect effect;
    private final ObservableList<Slider> sliders = FXCollections.observableArrayList();
    private final ObservableList<Slider> slidersUnmodifiable = FXCollections.unmodifiableObservableList(sliders);
    private VBox container = new VBox();
    private Label titleLabel = new Label();

    public GuitarEffectPane(Effect effect) {
        this.effect = effect;
        sliders.addListener((ListChangeListener<? super Slider>) change -> {
            container.getChildren().clear();
            container.getChildren().add(titleLabel);
            container.getChildren().addAll(sliders);
        });
        container.setAlignment(Pos.CENTER);
        container.getChildren().add(titleLabel);
    }

    public ObservableList<Slider> getSliders() {
        return slidersUnmodifiable;
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
            Slider gainSlider = new Slider(0, 200, 20);
            Slider thresholdSlider = new Slider(0, 1, 0.5);

            control.getSliders().add(gainSlider);
            control.getSliders().add(thresholdSlider);

            setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
            getChildren().add(control.container);
        }
    }

    private class DelaySkin extends SkinBase<GuitarEffectPane> {
        private DelaySkin(GuitarEffectPane control) {
            super(control);

            control.titleLabel.setText("Delay");
            Slider delaySlider = new Slider(0, 1, 0.5);
            Slider decaySlider = new Slider(0, 1, 0.5);

            control.getSliders().add(delaySlider);
            control.getSliders().add(decaySlider);

            setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)));
            getChildren().add(control.container);
        }
    }
}
