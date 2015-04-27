package spectrumanalyzer.dengn.spectrumanalyzer.models;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by Administrator on 2015-4-22.
 */
public class AudioRecorder {

    // Please note that currently 44100Hz is currently the only rate that is
    // guaranteed to work on all devices, but other rates such as 22050, 16000,
    // and 11025 may work on some devices.
    public static final int sampleRate = 44100;

    // CHANNEL_IN_MONO can guarantee to work in all devices
    public static final int channelConfig = AudioFormat.CHANNEL_IN_MONO;

    public static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecorder;
    //the minimum buffer size required for the successful creation of an AudioRecord object.
    private int bufferSize;


    private int accuracy;
    private short[] buffer;
    private int bufferReadResult;

    public AudioRecorder() {
        bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig,
                audioFormat);
        //Normally the returned bufferSize with this config is 4096.
        //Note that it doesn't have anything to do with the accuracy we put.
        audioRecorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                sampleRate, channelConfig, audioFormat, bufferSize);

    }

    public void startRecorder() {
        try {
            audioRecorder.startRecording();
        } catch (IllegalStateException e) {
            Log.e("Recording failed", e.toString());
        }
    }

    public void stopRecorder() {
        try {
            audioRecorder.release();
            //Without release, resuming after the stop would not be normal
            audioRecorder.stop();
        } catch (IllegalStateException e) {
            Log.e("Stop failed", e.toString());
        }
    }

    public void setAccuracy(int accu) {
        accuracy = accu;
    }

    public short[] getSamples() {
        buffer = new short[accuracy];
        bufferReadResult = audioRecorder.read(buffer, 0, accuracy);
        return buffer;

    }

    public int getBufferReadResult(){
        return bufferReadResult;
    }


}
