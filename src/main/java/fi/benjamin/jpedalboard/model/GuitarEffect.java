package fi.benjamin.jpedalboard.model;

public abstract class GuitarEffect {

    private boolean isActive;

    public abstract void processAudio(float[] outputLeftArray, float[] outputRightArray);
    public abstract void applySliderValues(double value1, double value2);

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
