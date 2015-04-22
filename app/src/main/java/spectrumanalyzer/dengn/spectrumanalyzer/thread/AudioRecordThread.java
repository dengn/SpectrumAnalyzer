package spectrumanalyzer.dengn.spectrumanalyzer.thread;

import android.content.Context;
import android.media.AudioRecord;

import java.util.logging.Handler;

import spectrumanalyzer.dengn.spectrumanalyzer.fft.FFT;
import spectrumanalyzer.dengn.spectrumanalyzer.spectrum.AudioRecorder;
import spectrumanalyzer.dengn.spectrumanalyzer.spectrum.Spectrum;

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

    public AudioRecordThread(Context context, Handler handler, int accuracy) {
        mContext = context;
        mHandler = handler;

    }

    private void getFreqByFFT(FFT fft_samples, short buffer_samples[]) {


        float fftRealArray[] = new float[mAccuracy];
        for (int i = index; i < mAudioRecorder.getBufferReadResult() + index; i++) {
            fftRealArray[i] = (float) buffer_samples[i - index]
                    / Short.MAX_VALUE;// 32768.0;
        }
        index += mAudioRecorder.getBufferReadResult();
        if (index == mAccuracy) {
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

            float maxVal = 0; // index of the bin with highest value
            int maxValIndex = 0; // index of the bin with highest value
            for (int i = 0; i < fft_samples.specSize(); i++) {
                if ((fft_samples.getBand(i)) > maxVal) {
                    maxVal = fft_samples.getBand(i);
                    maxValIndex = i;
                }

            }


            int final_freq = Math.round((float) maxValIndex
                    * ((float) mAudioRecorder.sampleRate / (float) mAccuracy));



        }

    }


    @Override
    public void run() {
        while (thread_running) {

            mAudioRecorder.setAccuracy(mAccuracy);
            mAudioRecorder.startRecorder();
            short[] buffer = mAudioRecorder.getSamples();

            FFT fft = new FFT(mAccuracy, AudioRecorder.sampleRate);
            getFreqByFFT(fft, buffer);

        }
    }

}
