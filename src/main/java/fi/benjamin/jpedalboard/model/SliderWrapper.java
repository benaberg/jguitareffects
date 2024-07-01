package fi.benjamin.jpedalboard.model;

import javafx.scene.control.Slider;

public class SliderWrapper {

    public enum Type {
        GAIN,
        THRESHOLD,
        DELAY,
        DECAY
    }

    private final Slider slider;
    private final Type type;

    public SliderWrapper(Slider slider, Type type) {
        this.slider = slider;
        this.type = type;
    }

    public Slider getSlider() {
        return slider;
    }

    public Type getType() {
        return type;
    }
}
