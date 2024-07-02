package fi.benjamin.jpedalboard.model;

public class DelayEffect extends GuitarEffect {

    private float[] circularBuffer;
    private float delay;
    private float decay;

    private int circularBufferMax;
    private int circularBufferIndex = 0;

    public DelayEffect() {
        this.delay = 0.5f;
        this.decay = 0.5f;
    }

    @Override
    public void processAudio(int sampleRate, float[] outputLeftArray, float[] outputRightArray) {
        this.circularBufferMax = (int) (sampleRate * delay);
        int delaySamples = (int) (sampleRate * delay);
        int numSamples = outputLeftArray.length;

        if (circularBuffer == null || circularBufferMax != circularBuffer.length) {
            circularBuffer = new float[circularBufferMax];
        }

        for (int i = 0; i < numSamples; i++) {
            int delayedIndex = Math.floorMod((circularBufferIndex - delaySamples), circularBufferMax);
            float value = outputLeftArray[i];
            outputLeftArray[i] = value + circularBuffer[delayedIndex];
            circularBuffer[circularBufferIndex] = value + decay * circularBuffer[circularBufferIndex];
            circularBufferIndex = (circularBufferIndex + 1) % circularBufferMax;
        }
    }

    @Override
    public void applySliderValues(float delay, float decay) {
        circularBuffer = new float[circularBufferMax];
        if (delay != -1) {
            this.delay = delay;
        }
        if (decay != -1) {
            this.decay = decay;
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
