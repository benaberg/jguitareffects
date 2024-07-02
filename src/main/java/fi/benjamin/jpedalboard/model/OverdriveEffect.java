package fi.benjamin.jpedalboard.model;

public class OverdriveEffect extends GuitarEffect {

    private float gain;
    private float threshold;

    public OverdriveEffect() {
        this.gain = 20f;
        this.threshold = 0.5f;
    }

    @Override
    public void processAudio(int sampleRate, float[] outputLeftArray, float[] outputRightArray) {
        for (int i = 0; i < outputLeftArray.length; i++) {
            outputLeftArray[i] = (float) Math.tanh(outputLeftArray[i] * gain);
            outputLeftArray[i] *= threshold;
        }
        for (int i = 0; i < outputRightArray.length; i++) {
            outputRightArray[i] = (float) Math.tanh(outputRightArray[i] * gain);
            outputRightArray[i] *= threshold;
        }
    }

    @Override
    public void applySliderValues(float gain, float threshold) {
        if (gain != -1) {
            this.gain = gain;
        }
        if (threshold != -1) {
            this.threshold = threshold;
        }
    }

    @Override
    public boolean isActive() {
        return super.isActive();
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
    }
}
