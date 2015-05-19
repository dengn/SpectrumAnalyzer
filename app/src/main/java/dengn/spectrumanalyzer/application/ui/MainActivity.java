package dengn.spectrumanalyzer.application.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.ref.WeakReference;

import dengn.spectrumanalyzer.application.fft.FFT;
import dengn.spectrumanalyzer.application.interfaces.FragmentCommunicator;
import dengn.spectrumanalyzer.application.models.Spectrum;
import dengn.spectrumanalyzer.application.thread.AudioRecordThread;
import dengn.spectrumanalyzer.application.utils.Constants;
import spectrumanalyzer.dengn.spectrumanalyzer.R;


public class MainActivity extends ActionBarActivity {

    //Interface
    public FragmentCommunicator fragmentCommunicator;

    //UI components objects


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

                    long time1 = SystemClock.elapsedRealtimeNanos();


                    //Avoid activity quits but fragment still waiting for transaction
                    if(!mainActivity.isFinishing()) {
                        SpectrumFragment spectrumFragment = SpectrumFragment.newInstance(spectrumJson);
                        mainActivity.getFragmentManager().beginTransaction().replace(R.id.container, spectrumFragment).commit();
                        //mainActivity.fragmentCommunicator.passDataToFragment(spectrumJson);
                    }

                    long time2 = SystemClock.elapsedRealtimeNanos();
                    if(Constants.DEBUG)
                        Log.d(Constants.TAG, "refreshing time: "+String.valueOf(time2-time1));

                    break;
            }
        }
    }
    private WeakHandler mHandler = new WeakHandler(this);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Ui Components
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SpectrumFragment())
                    .commit();
        }


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
