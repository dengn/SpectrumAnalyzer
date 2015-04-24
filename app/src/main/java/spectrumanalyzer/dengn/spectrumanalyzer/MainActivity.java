package spectrumanalyzer.dengn.spectrumanalyzer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.quist.app.errorreporter.ExceptionReporter;
import spectrumanalyzer.dengn.spectrumanalyzer.fft.FFT;
import spectrumanalyzer.dengn.spectrumanalyzer.spectrum.Spectrum;
import spectrumanalyzer.dengn.spectrumanalyzer.thread.AudioRecordThread;
import spectrumanalyzer.dengn.spectrumanalyzer.utils.Constants;


public class MainActivity extends ActionBarActivity {


    //UI components objects
    private LineChart spectrumChart;

    //function objects
    private Spectrum mSpectrum = new Spectrum();

    private AudioRecordThread mAudioRecordThread;


    static class WeakHandler extends Handler{

        WeakReference<MainActivity> mActivityReference;

        public WeakHandler(MainActivity activity) {
            mActivityReference= new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            MainActivity mainActivity = mActivityReference.get();
            switch (msg.what) {
                case 0:
                    String spectrumJson = (String) msg.obj;
//                    if(Constants.DEBUG)
//                        Log.d(Constants.TAG, "spectrum json: "+spectrumJson);
                    mainActivity.mSpectrum = Constants.gson.fromJson(spectrumJson, Spectrum.class);

                    mainActivity.setSignalData(mainActivity.mSpectrum, mainActivity.spectrumChart);
                    mainActivity.spectrumChart.invalidate();

                    break;
            }
        }
    }
    private WeakHandler mHandler = new WeakHandler(this);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ExceptionReporter reporter = ExceptionReporter.register(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Ui Components
        spectrumChart = (LineChart) findViewById(R.id.spectrumChart);
        spectrumChart.setDescription("");

        YAxis leftAxis = spectrumChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(2000f);
        leftAxis.setAxisMinValue(-2000f);
        leftAxis.setStartAtZero(false);
        spectrumChart.getAxisRight().setEnabled(false);


        //Create and start AudioRecord Thread
        mAudioRecordThread = new AudioRecordThread(this, mHandler, FFT.ACCURACY_LOWEST);
        mAudioRecordThread.start();
    }


    private void setSignalData(Spectrum spectrum, LineChart chart) {

        short[] signalSamples = spectrum.getSignalSamples();

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < signalSamples.length; i++) {
            xVals.add("");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < signalSamples.length; i++) {

            float val = signalSamples[i];
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "Signal Samples");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
//        set1.enableDashedLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
//        set1.setLineWidth(1f);
//        set1.setCircleSize(3f);
//        set1.setDrawCircleHole(false);
//        set1.setValueTextSize(9f);
//        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        chart.setData(data);
    }

    private void setSpectrumData(Spectrum spectrum, LineChart chart) {

        int[] frequencies = spectrum.getFrequencies();
        float[] amplitudes = spectrum.getAmplitudes();

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < frequencies.length; i++) {
            xVals.add(String.valueOf(frequencies[i]) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < amplitudes.length; i++) {

            float val = amplitudes[i];
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "Spectrum");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
//        set1.enableDashedLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
//        set1.setLineWidth(1f);
//        set1.setCircleSize(3f);
//        set1.setDrawCircleHole(false);
//        set1.setValueTextSize(9f);
//        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        chart.setData(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAudioRecordThread.restartThread();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (Constants.DEBUG)
            Log.d(Constants.TAG, "on Stop");
        mAudioRecordThread.stopThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constants.DEBUG)
            Log.d(Constants.TAG, "on Destroy");
        mAudioRecordThread.stopThread();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
