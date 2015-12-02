package com.ciandt.armcontrol.armcontrol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float orientation[] = new float[3];
    private float lastOrientation[] = new float[3];
    private Handler handler;
    private Runnable runnable;
    private boolean moveOmbro = false;
    private boolean moveCotovelo = false;
    private boolean movePunho = false;
    private boolean moveDedos = false;

    private TextView info;
    private Button btnOmbro, btnPunho, btnCotovelo, btnDedos;

    int incBraco = 0;
    int incAnte = 0;
    int ajusteIncBraco = 0;
    int ajusteIncAnte = 0;

    Simulator simulator;
    private class Simulator extends View {
        Paint paint = new Paint();

        public Simulator(Context context) {
            super(context);
            paint.setAntiAlias(true);
            paint.setTextSize(20);
            paint.setStyle(Paint.Style.STROKE);
        }
        @Override
        public void onDraw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int centerx = width/2;
            int centery = height/2;

            int marginTop = 20;
            int tamBraco = 300;
            int tamAnteBraco = 300;
            int tamMao = 65;

            incBraco = incBraco + ajusteIncBraco;
            incAnte = incAnte + ajusteIncAnte;

            int limites = 400;
            if (incBraco<0)
                incBraco=0;

            if (incBraco>limites)
                incBraco=limites;

            if (incAnte>limites)
                incAnte=limites;

            if (incAnte<0)
                incAnte=0;


            paint.setColor(Color.RED);
            paint.setStrokeWidth(50);
            //BRACO
            int xIniBraco = 200; //fixo
            int yIniBraco = marginTop; //fixo
            int xFimBraco = xIniBraco + incBraco;
            int yFimBraco =  marginTop + tamBraco - (incBraco/2);
            canvas.drawLine(xIniBraco, yIniBraco, xFimBraco, yFimBraco, paint);


            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(30);
            //ANTE BRACO
            int xFimAnte = xFimBraco + (incBraco/2) + incAnte;
            int yFimAnte = yFimBraco + tamAnteBraco - (incBraco/2) - (incAnte/2);
            canvas.drawLine(xFimBraco, yFimBraco, xFimAnte, yFimAnte, paint);


            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(60);
            //MAO
            canvas.drawLine(xFimAnte, yFimAnte, xFimAnte, yFimAnte + tamMao, paint);

            ajusteIncBraco = 0;
            ajusteIncAnte = 0;
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        info = (TextView) findViewById(R.id.info);
        info.setText("Mantenha o celular na horizontal, segure o botão e incline/decline para mover");

        btnOmbro = (Button) findViewById(R.id.btnOmbro);
        btnOmbro.setOnTouchListener(new View.OnTouchListener() { @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moveOmbro = true;
                    resetLastOrientation();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    moveOmbro = false;
                }
                return false;
            }});

        btnPunho = (Button) findViewById(R.id.btnPunho);
        btnPunho.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    movePunho = true;
                    resetLastOrientation();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    movePunho = false;
                }
                return false;
            }
        });

        btnCotovelo = (Button) findViewById(R.id.btnCotovelo);
        btnCotovelo.setOnTouchListener(new View.OnTouchListener() { @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moveCotovelo = true;
                    resetLastOrientation();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    moveCotovelo = false;
                }
                return false;
        }});

        btnDedos = (Button) findViewById(R.id.btnDedos);
        btnDedos.setOnTouchListener(new View.OnTouchListener() { @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    moveDedos = true;
                    resetLastOrientation();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    moveDedos = false;
                }
                return false;
            }});

        // Register the sensor listeners
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // set Draw Fragment
        /*
        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        DrawFragment mDrawFragment = DrawFragment.getInstance();
        mFragmentTransaction.add(R.id.fragmentSimulate , mDrawFragment, "draw");
        mFragmentTransaction.commit();
        */

        simulator = new Simulator(this);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rect);
        relativeLayout.addView(simulator);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                execTimer();
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(runnable, 100);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        handler.removeCallbacks(runnable);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                SensorManager.getOrientation(R, orientation);
                //azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                //Log.i("Orientation: ", orientation[0] + " , " + orientation[1] + " , " + orientation[2]);
            }
        }

        //mCustomDrawableView.invalidate();
    }


    private void execTimer() {
        //info.setText( String.format("%.0f", orientation[0]*100) );

        if (moveOmbro) {
            ajusteIncBraco = (int) (orientation[1] * 200);
            ajusteIncBraco = ajusteIncBraco * (-1);
            simulator.invalidate();
        }

        if (moveCotovelo) {
            ajusteIncAnte = (int) (orientation[1] * 200);
            ajusteIncAnte = ajusteIncAnte  * (-1);
            simulator.invalidate();
        }
    }

    private void resetLastOrientation() {
        for (int i = 0; i <= 2; i++) {
            lastOrientation[i] = orientation[i];
        }
    }

}