package com.arkadygamza.shakedetector;

import android.graphics.Color;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import rx.Observable;
import rx.Subscription;

/**
 * Draws graph of sensor events
 */
public class SensorPlotter {
    public static final int MAX_DATA_POINTS = 50;
    public static final int VIEWPORT_SECONDS = 5;
    public static final int FPS = 10;

    @NonNull
    private final String mName;

    private final long mStart = System.currentTimeMillis();

    protected final LineGraphSeries<DataPoint> mSeriesX;
    protected final LineGraphSeries<DataPoint> mSeriesY;
    protected final LineGraphSeries<DataPoint> mSeriesZ;

    protected  final  LineGraphSeries<DataPoint> mSeriesXX;
    protected final LineGraphSeries<DataPoint> mSeriesYY;
    protected final LineGraphSeries<DataPoint> mSeriesZZ;
    private final Observable<SensorEvent> mSensorEventObservable;
    private long mLastUpdated = mStart;
    private Subscription mSubscription;
    private String state;
    private float On_1 = 1;
    private float altha = 0.1f;

    public SensorPlotter(@NonNull String name, @NonNull  GraphView graphView,
                         @NonNull Observable<SensorEvent> sensorEventObservable,String state) {
        this.state = state;
        mName = name;
        mSensorEventObservable = sensorEventObservable;

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(VIEWPORT_SECONDS * 1000); // number of ms in viewport

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(-20);
        graphView.getViewport().setMaxY(20);

        graphView.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graphView.getGridLabelRenderer().setVerticalLabelsVisible(false);

        mSeriesX = new LineGraphSeries<>();
        mSeriesY = new LineGraphSeries<>();
        mSeriesZ = new LineGraphSeries<>();
        mSeriesXX=new LineGraphSeries<>();
        mSeriesYY=new LineGraphSeries<>();
        mSeriesZZ=new LineGraphSeries<>();
        mSeriesX.setColor(Color.RED);
        mSeriesY.setColor(Color.GREEN);
        mSeriesZ.setColor(Color.BLUE);
        mSeriesXX.setColor(Color.YELLOW);
        mSeriesYY.setColor(Color.WHITE);
        mSeriesZZ.setColor(Color.MAGENTA);

        graphView.addSeries(mSeriesX);
        graphView.addSeries(mSeriesY);
        graphView.addSeries(mSeriesZ);
        graphView.addSeries(mSeriesXX);
        graphView.addSeries(mSeriesYY);
        graphView.addSeries(mSeriesZZ);
    }


    public void onResume(){
        mSubscription = mSensorEventObservable.subscribe(this::onSensorChanged);
    }

    public void onPause(){
        mSubscription.unsubscribe();
    }

    private void onSensorChanged(SensorEvent event) {
        if (!canUpdateUi()) {
            return;
        }
        switch (state) {
            case "X":
//                double d=event.values[0]+1d;
//                double dfiltr=(double)(On_1 + altha * (d - On_1));
//                appendData(mSeriesX, dfiltr);
                appendData(mSeriesY, event.values[1]);
                appendData(mSeriesZ, event.values[2]);
//                appendData(mSeriesXX,d);
                break;
            case "Y":
                double dy=event.values[1]+1d;
                double dfiltry=(double)(On_1 + altha * (dy - On_1));
                appendData(mSeriesY, event.values[1]);
                appendData(mSeriesZ, event.values[2]);
                appendData(mSeriesX, event.values[0]);
                appendData(mSeriesY, dy);
             appendData(mSeriesYY,dfiltry);
                break;
            case "Z":
                appendData(mSeriesX, event.values[0]);
                appendData(mSeriesY, event.values[1]);
                appendData(mSeriesZ, event.values[2]);
                break;
            case "DEFAULT":
                appendData(mSeriesX, event.values[0]);
                appendData(mSeriesY, event.values[1]);
                appendData(mSeriesZ, event.values[2]);


                break;
        }
    }

    private boolean canUpdateUi() {
        long now = System.currentTimeMillis();
        if (now - mLastUpdated < 1000 / FPS) {
            return false;
        }
        mLastUpdated = now;
        return true;
    }

    private void appendData(LineGraphSeries<DataPoint> series, double value) {
        series.appendData(new DataPoint(getX(), value), true, MAX_DATA_POINTS);
    }

    public void setState(String s) {
        this.state = s;
    }
    private long getX() {
        return System.currentTimeMillis() - mStart;
    }
}
