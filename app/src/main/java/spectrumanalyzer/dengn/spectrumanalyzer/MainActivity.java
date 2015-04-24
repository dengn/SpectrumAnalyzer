package spectrumanalyzer.dengn.spectrumanalyzer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import spectrumanalyzer.dengn.spectrumanalyzer.fft.FFT;
import spectrumanalyzer.dengn.spectrumanalyzer.spectrum.Spectrum;
import spectrumanalyzer.dengn.spectrumanalyzer.thread.AudioRecordThread;
import spectrumanalyzer.dengn.spectrumanalyzer.utils.Constants;


public class MainActivity extends ActionBarActivity {


    //UI components objects
    private SurfaceView mSpectrumView;
    private SurfaceHolder mSpectrumViewHolder;

    private TextView mFreqView;

    //function objects
    private Spectrum mSpectrum = new Spectrum();

    private AudioRecordThread mAudioRecordThread;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String spectrumJson = (String) msg.obj;
                    if(Constants.DEBUG)
                        Log.d(Constants.TAG, "spectrum json: "+spectrumJson);
                    mSpectrum = Constants.gson.fromJson(spectrumJson, Spectrum.class);


                    break;
            }
        }
    };

    //Drawing objects


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFreqView = (TextView) findViewById(R.id.freq);
        mFreqView.setText("123456789");

        mSpectrumView = (SurfaceView) findViewById(R.id.spectrum);
        mSpectrumViewHolder = mSpectrumView.getHolder();

        //Create and start AudioRecord Thread
        mAudioRecordThread = new AudioRecordThread(this, mHandler, FFT.ACCURACY_MIDEUM);
        mAudioRecordThread.start();
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
