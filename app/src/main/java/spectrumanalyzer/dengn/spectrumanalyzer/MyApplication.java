package spectrumanalyzer.dengn.spectrumanalyzer;
import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by OLEDCOMM on 27/04/2015.
 */
@ReportsCrashes(formUri = "http://54.69.185.28/spectrum/error.php")
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        super.onCreate();
        ACRA.init(this);
    }
}
