package fi.benjamin.jpedalboard;

import com.synthbot.jasiohost.AsioChannel;
import com.synthbot.jasiohost.AsioDriver;
import com.synthbot.jasiohost.AsioDriverListener;
import com.synthbot.jasiohost.AsioDriverState;
import fi.benjamin.jpedalboard.model.GuitarEffect;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AudioListener implements AsioDriverListener {

    public enum Mode {
        STEREO,
        MONO
    }

    private static final String INPUT_CHANNEL_1 = "Input 1";
    private static final String INPUT_CHANNEL_2 = "Input 2";
    private static final String OUTPUT_CHANNEL_1 = "Output 1";
    private static final String OUTPUT_CHANNEL_2 = "Output 2";

    private final Set<AsioChannel> activeChannels = new HashSet<>();
    private final List<GuitarEffect> effects;
    private final AsioDriver asioDriver;
    private final int sampleRate;
    private final int bufferSize;

    private Mode mode;

    public AudioListener(String driverName, Mode mode, List<GuitarEffect> effects) {
        this.mode = mode;
        this.effects = effects;
        asioDriver = AsioDriver.getDriver(driverName);
        sampleRate = (int) asioDriver.getSampleRate();
        bufferSize = asioDriver.getBufferPreferredSize();
        asioDriver.addAsioDriverListener(this);
        for(int i = 0; i < asioDriver.getNumChannelsInput(); i ++) {
            activeChannels.add(asioDriver.getChannelInput(i));
        }
        for(int i = 0; i < asioDriver.getNumChannelsOutput(); i ++) {
            activeChannels.add(asioDriver.getChannelOutput(i));
        }
        asioDriver.createBuffers(activeChannels);
        asioDriver.start();
    }

    @Override
    public void bufferSwitch(long systemTime, long samplePosition, Set<AsioChannel> channels) {
        float[] outputLeftArray = new float[bufferSize];
        float[] outputRightArray = new float[bufferSize];

        channels.stream().filter(AsioChannel::isInput).forEach(channel -> {
            for (int i = 0; i < bufferSize; i++) {
                if (channel.getChannelName().equals(INPUT_CHANNEL_1)) {
                    outputLeftArray[i] += ((float) channel.getByteBuffer().getInt()) / Integer.MAX_VALUE;
                }
                else if (channel.getChannelName().equals(INPUT_CHANNEL_2) && mode.equals(Mode.STEREO)) {
                    outputRightArray[i] += ((float) channel.getByteBuffer().getInt()) / Integer.MAX_VALUE;
                }
            }
        });
        setEffects(channels.stream().filter(channel -> !channel.isInput()).collect(Collectors.toSet()), outputLeftArray, outputRightArray);
    }

    @Override
    public void bufferSizeChanged(int bufferSize) {
        System.out.println("bufferSizeChanged() callback received.");
    }

    @Override
    public void latenciesChanged(int inputLatency, int outputLatency) {
        System.out.println("latenciesChanged() callback received.");
    }

    @Override
    public void resetRequest() {
        new Thread(() -> {
            System.out.println("resetRequest() callback received. Returning driver to INITIALIZED state.");
            asioDriver.returnToState(AsioDriverState.INITIALIZED);
        }).start();
    }

    @Override
    public void resyncRequest() {
      System .out.println("resyncRequest() callback received.");
    }

    @Override
    public void sampleRateDidChange(double sampleRate) {
        System.out.println("sampleRateDidChange() callback received.");
    }

    private void setEffects(Set<AsioChannel> outputChannels, float[] outputLeftArray, float[] outputRightArray) {
        effects.stream().filter(GuitarEffect::isActive).forEach(effect -> effect.processAudio(sampleRate, outputLeftArray, outputRightArray));
        outputChannels.forEach(channel -> {
            if (channel.getChannelName().equals(OUTPUT_CHANNEL_1) || mode.equals(Mode.MONO)) {
                channel.write(outputLeftArray);
            }
            else if (channel.getChannelName().equals(OUTPUT_CHANNEL_2)) {
                channel.write(outputRightArray);
            }
        });
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void stop() {
        asioDriver.shutdownAndUnloadDriver();
    }
}
