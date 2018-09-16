package com.arkadygamza.shakedetector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {

    private final List<SensorPlotter> mPlotters = new ArrayList<>(3);

    private Observable<?> mShakeObservable;
    private Subscription mShakeSubscription;
    public String state="DEFAULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPlotters();

        mShakeObservable = ShakeDetector.create(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.line_x:
                state ="X";
                changeState(state);
                return true;

            case  R.id.line_y:
                state ="Y";
                changeState(state);
                return true;

            case R.id.line_z:
                state ="Z";
                changeState(state);
                return true;

            case R.id.line_default:
                state ="DEFAULT";
                changeState(state);
                return true;
            default:
                state ="DEFAULT";
                changeState(state);
                return true;
        }
    }

    public void changeState(String state) {
        mPlotters.get(0).setState(state);

    }

    private void setupPlotters() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> gravSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);


        mPlotters.add(new SensorPlotter("GRAV", (GraphView) findViewById(R.id.graph1), SensorEventObservableFactory.createSensorEventObservable(gravSensors.get(0), sensorManager), state));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Observable.from(mPlotters).subscribe(SensorPlotter::onResume);
        mShakeSubscription = mShakeObservable.subscribe((object) -> Utils.beep());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Observable.from(mPlotters).subscribe(SensorPlotter::onPause);
        mShakeSubscription.unsubscribe();
    }
}
