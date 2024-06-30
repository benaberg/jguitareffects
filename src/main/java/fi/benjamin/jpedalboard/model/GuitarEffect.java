package fi.benjamin.jpedalboard.model;

public abstract class GuitarEffect {

    public abstract void processAudio(float[] outputLeftArray, float[] outputRightArray);
}
