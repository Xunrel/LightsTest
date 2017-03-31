package com.trott.lightstest;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager _manager;
    private Sensor _lightSensor;
    private TextView _lxTextView;
    private TextView _blinkCountTextView;
    private TextView _lxValuesTextView;
    private Button _blinkButton;
    private Button _registerButton;
    private String _lxTextTemplate = "Current lx: %s";
    private String _blinkCountTextTemplate = "Blink counts: %s";
    private Animation _blinkAnimation;
    private RelativeLayout _blinkLayout;
    private boolean _isBlinking = false;
    private boolean _isRegistering = false;
    private Random _random;
    private ArrayList<Float> _lxValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        _lightSensor = _manager.getDefaultSensor(Sensor.TYPE_LIGHT);

        _blinkLayout = (RelativeLayout)findViewById(R.id.blinkLayout);
        _blinkLayout.setBackgroundColor(Color.BLACK);
        _lxTextView = (TextView)findViewById(R.id.lxTextView);
        _blinkCountTextView = (TextView)findViewById(R.id.blinkCountTextView);
        _lxValuesTextView = (TextView)findViewById(R.id.lxValuesTextView);
        _blinkButton = (Button)findViewById(R.id.blinkButton);
        _registerButton = (Button)findViewById(R.id.registerButton);

        _lxValues = new ArrayList<>();
        _random = new Random();
        createBlinkAnimation();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lxValue = event.values[0];
        if(_isRegistering) {
            _lxValues.add(lxValue);
        }
        _lxTextView.setText(String.format(_lxTextTemplate, lxValue));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Currently nothing to do
    }

    @Override
    protected void onPause() {
        super.onPause();
        _manager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _manager.registerListener(this, _lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
        int minDelay = _lightSensor.getMinDelay();
//        int maxDelay = _lightSensor.getMaxDelay();
    }

    public void toggleRegistering(View view) {
        _isRegistering = !_isRegistering;
        if (!_isRegistering && !_lxValues.isEmpty()){
            showLxValues();
        }
    }

    public void toggleBlinking(View view) {


        if (_isBlinking) {
            _isBlinking = false;
            _blinkLayout.clearAnimation();
            _blinkButton.setText("Start Blinking");
        } else {
            int blinkCounter = _random.nextInt(10);

            if (blinkCounter == 0) blinkCounter = 1;
            int calculatedRepeat = (blinkCounter * 2) - 1;

            _blinkCountTextView.setText(String.format(_blinkCountTextTemplate, blinkCounter));
            _blinkAnimation.setRepeatCount(calculatedRepeat);
            _blinkLayout.startAnimation(_blinkAnimation);
            _blinkButton.setText("Stop");
        }
    }

    private void blink(int times, int duration) {
        _blinkAnimation.setDuration(duration);
        _blinkAnimation.setRepeatCount(times);
        _blinkLayout.startAnimation(_blinkAnimation);
    }

    private void createBlinkAnimation() {
        _blinkAnimation = new AlphaAnimation(1, 0);
        _blinkAnimation.setInterpolator(new LinearInterpolator());
        _blinkAnimation.setDuration(170);
        _blinkAnimation.setRepeatCount(Animation.INFINITE);
        _blinkAnimation.setRepeatMode(Animation.REVERSE);

        _blinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                _isBlinking = true;
                _isRegistering = true;
                Log.d("Blink Animation", "Starting animation");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                _blinkLayout.clearAnimation();
                _blinkButton.setText("Start Blinking");
                Log.d("Blink Animation", "Stopping animation");
                _isBlinking = false;
                _isRegistering = false;
                showLxValues();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d("Blink Animation", "Repeating animation");
            }
        });
    }

    private void showLxValues() {
        String lxValuesText = "lx Values.\n";
        for(int i = 0; i < _lxValues.size(); i ++) {
            lxValuesText += String.format("%s\n", _lxValues.get(i));
        }
        _lxValuesTextView.setText(lxValuesText);
        _lxValues.clear();
    }
}
