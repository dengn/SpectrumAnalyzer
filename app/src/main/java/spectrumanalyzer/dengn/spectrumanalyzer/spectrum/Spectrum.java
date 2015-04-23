package spectrumanalyzer.dengn.spectrumanalyzer.spectrum;

/**
 * Created by Administrator on 2015-4-22.
 */
public class Spectrum{


    private short[] mSignalSamples;
    private float[] mAmplitudes;
    private float[] mFrequencies;



    public Spectrum() {


    }

    public Spectrum(short[] signalSamples, float[] amplitudes, float[] frequencies) {
        mSignalSamples = signalSamples;
        mAmplitudes = amplitudes;
        mFrequencies = frequencies;
    }

    public short[] getSignalSamples(){
        return mSignalSamples;
    }

    public void setSignalSamples(short[] signalSamples){
        mSignalSamples = signalSamples;
    }

    public float[] getAmplitudes(){
        return mAmplitudes;
    }

    public void setAmplitudes(float[] amplitudes){
        mAmplitudes = amplitudes;
    }

    public float[] getFrequencies(){
        return mFrequencies;
    }

    public void setFrequencies(float[] frequencies){
        mFrequencies = mFrequencies;
    }



}
