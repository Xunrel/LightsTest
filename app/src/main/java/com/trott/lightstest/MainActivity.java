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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager _manager;
    private Sensor _lightSensor;
    private TextView _lxTextView;
    private TextView _blinkCountTextView;
    private Button _blinkButton;
    private String _lxTextTemplate = "Current lx: %s";
    private String _blinkCountTextTemplate = "Blink counts: %s";
    private Animation _blinkAnimation;
    private RelativeLayout _blinkLayout;
    private ConstraintLayout _constraintLayout;
    private boolean _isBlinking = false;
    private Random _random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        _lightSensor = _manager.getDefaultSensor(Sensor.TYPE_LIGHT);

        _blinkLayout = (RelativeLayout)findViewById(R.id.blinkLayout);
        _blinkLayout.setBackgroundColor(Color.BLACK);
//        _constraintLayout = (ConstraintLayout)findViewById(R.id.constraintLayout);
//        _constraintLayout.setBackgroundColor(Color.BLACK);
        _lxTextView = (TextView)findViewById(R.id.lxTextView);
        _blinkCountTextView = (TextView)findViewById(R.id.blinkCountTextView);
        _blinkButton = (Button)findViewById(R.id.blinkButton);

        _random = new Random();
        createBlinkAnimation();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lxValue = event.values[0];
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
        _manager.registerListener(this, _lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void toggleBlinking(View view) {


        if (_isBlinking) {
            _isBlinking = false;
            _blinkLayout.clearAnimation();
            _blinkButton.setText("Start Blinking");
        } else {
            int blinkCounter = _random.nextInt(10) + 1;
            _blinkCountTextView.setText(String.format(_blinkCountTextTemplate, blinkCounter));
            _blinkAnimation.setRepeatCount(blinkCounter);
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
        _blinkAnimation.setDuration(50);
        _blinkAnimation.setRepeatCount(Animation.INFINITE);
        _blinkAnimation.setRepeatMode(Animation.REVERSE);

        _blinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                _isBlinking = true;
                Log.d("Blink Animation", "Starting animation");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                _blinkLayout.clearAnimation();
                _blinkButton.setText("Start Blinking");
                Log.d("Blink Animation", "Stopping animation");
                _isBlinking = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d("Blink Animation", "Repeating animation");
            }
        });
    }
}
