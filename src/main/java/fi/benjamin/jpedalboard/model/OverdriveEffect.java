package fi.benjamin.jpedalboard.model;

public class OverdriveEffect extends GuitarEffect {

    private double gain;
    private double threshold;

    public OverdriveEffect() {
        this.gain = 20;
        this.threshold = 0.5;
    }

    @Override
    public void processAudio(float[] outputLeftArray, float[] outputRightArray) {
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
    public void applySliderValues(double gain, double threshold) {
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
