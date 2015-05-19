package dengn.spectrumanalyzer.application.ui;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import dengn.spectrumanalyzer.application.interfaces.FragmentCommunicator;
import dengn.spectrumanalyzer.application.models.Spectrum;
import dengn.spectrumanalyzer.application.utils.Constants;
import spectrumanalyzer.dengn.spectrumanalyzer.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpectrumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpectrumFragment extends Fragment implements FragmentCommunicator {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String SPECTRUM_STRING = "spectrum";

    private Spectrum spectrum;

    //UI
    private LinearLayout chartSignal;
    private LinearLayout chartSpectrum;



    //
    private Context context;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SpectrumFragment.
     */
    public static SpectrumFragment newInstance(String param1) {
        SpectrumFragment fragment = new SpectrumFragment();
        Bundle args = new Bundle();
        args.putString(SPECTRUM_STRING, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SpectrumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String spectrumString = getArguments().getString(SPECTRUM_STRING);
            spectrum = Constants.gson.fromJson(spectrumString, Spectrum.class);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_spectrum, container, false);
        chartSpectrum = (LinearLayout) v.findViewById(R.id.chart_spectrum);
        if(spectrum!=null) {
            chartSpectrum.addView(createSpectrumGraph());
        }
        return v;

    }

    //Since Fragment is Activity dependent you need Activity context in various cases
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = getActivity();
        ((MainActivity)context).fragmentCommunicator = this;
    }

    //FragmentCommunicator interface implementation
    @Override
    public void passDataToFragment(String spectrumString){
        spectrum = Constants.gson.fromJson(spectrumString, Spectrum.class);
        if(spectrum!=null) {
            chartSpectrum.removeAllViews();
            chartSpectrum.addView(createSpectrumGraph());
        }
    }



    private View createSpectrumGraph() {
        // We start creating the XYSeries to plot the temperature
        XYSeries series = new XYSeries("Spectrum");


        for(int i=0;i<spectrum.getFrequencies().length;i++){
            series.add((double)spectrum.getFrequencies()[i], Math.log((double)spectrum.getAmplitudes()[i]));
        }

        // Now we create the renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.RED);
        // Include low and max value
        renderer.setDisplayBoundingPoints(true);
        //renderer.setDisplayChartValues(true);
        // we add point markers
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(3);


        // Now we add our series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        // Finaly we create the multiple series renderer to control the graph
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        // Disable Pan on two axis
        mRenderer.setPanEnabled(false, false);
        mRenderer.setYAxisMax(60);
        mRenderer.setYAxisMin(-20);
        mRenderer.setShowGrid(true); // we show the grid
        GraphicalView chartView = ChartFactory.getLineChartView(getActivity(), dataset, mRenderer);

        return chartView;
    }



    private View createSignalGraph() {
        // We start creating the XYSeries to plot the temperature
        XYSeries series = new XYSeries("Signal");


        for(int i=0;i<spectrum.getSignalSamples().length;i++){
            series.add((double)i, (double)spectrum.getSignalSamples()[i]);
        }

        // Now we create the renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.RED);
        // Include low and max value
        renderer.setDisplayBoundingPoints(true);
        // we add point markers
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(3);


        // Now we add our series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        // Finaly we create the multiple series renderer to control the graph
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        // Disable Pan on two axis
        mRenderer.setPanEnabled(false, false);
        mRenderer.setYAxisMax(35);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowGrid(true); // we show the grid
        GraphicalView chartView = ChartFactory.getLineChartView(getActivity(), dataset, mRenderer);

        return chartView;
    }

}
