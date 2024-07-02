package fi.benjamin.jpedalboard.model;

public abstract class GuitarEffect {

    private boolean isActive;

    public abstract void processAudio(int sampleRate, float[] outputLeftArray, float[] outputRightArray);
    public abstract void applySliderValues(float value1, float value2);

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
