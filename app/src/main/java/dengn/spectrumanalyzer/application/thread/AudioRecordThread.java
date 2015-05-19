package dengn.spectrumanalyzer.application.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import dengn.spectrumanalyzer.application.fft.FFT;
import dengn.spectrumanalyzer.application.models.AudioRecorder;
import dengn.spectrumanalyzer.application.models.Spectrum;
import dengn.spectrumanalyzer.application.utils.Constants;

/**
 * Created by Administrator on 2015-4-22.
 */
public class AudioRecordThread extends Thread {

    private Context mContext;
    private Handler mHandler;
    private boolean thread_running = true;

    private AudioRecorder mAudioRecorder = new AudioRecorder();
    private Spectrum mSpectrum = new Spectrum();

    private int mAccuracy;


    private int index = 0;

    /**
     *
     * @param context
     * @param handler
     * @param accuracy
     */
    public AudioRecordThread(Context context, Handler handler, int accuracy) {
        mContext = context;
        mHandler = handler;
        mAccuracy = accuracy;
    }

    /**
     *
     * @param fft_samples
     * @param buffer_samples
     */
    private void getFreqByFFT(FFT fft_samples, short buffer_samples[]) {


        float fftRealArray[] = new float[mAccuracy];
        for (int i = index; i < mAudioRecorder.getBufferReadResult() + index; i++) {
            fftRealArray[i] = (float) buffer_samples[i - index]
                    / Short.MAX_VALUE;// 32768.0;
        }
        index += mAudioRecorder.getBufferReadResult();

//        if((index>mAccuracy)||(index<0)){
//            index = 0;
//        }
        if (index >= mAccuracy) {
            index = 0;
            // apply windowing
            for (int i = 0; i < mAccuracy / 2; ++i) {
                // Calculate & apply window symmetrically around center
                // point
                // Hanning (raised cosine) window
                float winval = (float) (0.5 + 0.5 * Math.cos(Math.PI
                        * (float) i / (float) (mAccuracy / 2)));
                if (i > mAccuracy / 2)
                    winval = 0;
                fftRealArray[mAccuracy / 2 + i] *= winval;
                fftRealArray[mAccuracy / 2 - i] *= winval;

                // fftRealArrayString+=String.valueOf(fftRealArray[i])+" ";
            }
            fftRealArray[0] = 0;
            fft_samples.forward(fftRealArray);



            //get spectrum amplitudes and frequencies -> X Y values
            float[] amplitudes = new float[fft_samples.specSize()];
            int[] frequencies = new int[fft_samples.specSize()];

            for (int i = 0; i < fft_samples.specSize(); i++) {
                amplitudes[i] = fft_samples.getBand(i);
                frequencies[i] = Math.round((float) i
                        * ((float) mAudioRecorder.sampleRate / (float) mAccuracy));

             }

            mSpectrum.setAmplitudes(amplitudes);
            mSpectrum.setFrequencies(frequencies);

            //Send result back to main UI thread
            Message msg = Message.obtain();
            msg.what = 0;
            msg.obj = Constants.gson.toJson(mSpectrum);
            mHandler.sendMessage(msg);

        }

    }

    public void stopThread(){
        mAudioRecorder.stopRecorder();
        thread_running = false;

        if ((this != null)&&this.isAlive()&&!this.isInterrupted()) {
            this.interrupt();
        }
    }

    public void restartThread(){
        mAudioRecorder.startRecorder();
        thread_running = true;

        if ((this!=null)&&!this.isAlive()&&this.isInterrupted()) {
            this.start();
        }
    }

    @Override
    public void run() {
        while (thread_running) {

            mAudioRecorder.setAccuracy(mAccuracy);
            mAudioRecorder.startRecorder();
            short[] buffer = mAudioRecorder.getSamples();

            mSpectrum.setSignalSamples(buffer);

            FFT fft = new FFT(mAccuracy, AudioRecorder.sampleRate);
            getFreqByFFT(fft, buffer);

        }
    }

}
