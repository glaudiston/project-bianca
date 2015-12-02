package com.ciandt.armcontrol.armcontrol;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DrawFragment extends Fragment {
    DrawView  drawView;

    float var0;
    float var1;
    float var2;

    public class DrawView extends View {
        Paint paint = new Paint();
        public DrawView(Context context) {
            super(context);
            paint.setColor(0xff00ff00);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setAntiAlias(true);
        };

        protected void onDraw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int centerx = width/2;
            int centery = height/2;
            canvas.drawLine(centerx, 0, centerx, height, paint);
            canvas.drawLine(0, centery, width, centery, paint);

            // Rotate the canvas with the azimut

            canvas.rotate(-1*360/(2*3.14159f), centerx, centery);

            paint.setColor(0xff0000ff);
            canvas.drawLine(centerx, -1000, centerx, +1000, paint);
            canvas.drawLine(-1000, centery, 1000, centery, paint);
            canvas.drawText("N", centerx+5, centery-10, paint);
            canvas.drawText("S", centerx-10, centery+15, paint);
            paint.setColor(0xff00ff00);
        }
    }

    public DrawFragment() {
    }

    /**
     * Returns Instance of DrawFragment
     *
     * @return Instance of DrawFragment
     */
    public static DrawFragment getInstance() {
        DrawFragment fragment = new DrawFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.layout_drawing_fragment,
                container, false);
        //lets keep a reference of DrawView
        drawView = (DrawView ) _view.findViewById(R.id.drawing);
        return _view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void simulate(float var0, float var1, float var2) {
        this.var0 = var0;
        this.var1 = var1;
        this.var2 = var2;
        drawView.invalidate();
    }
}