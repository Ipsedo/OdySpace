package com.samuelberrien.odyspace;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by samuel on 16/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer renderer;

    private final float TOUCH_SCALE_FACTOR_MOVE = 0.001f;
    private float mPreviousX;
    private float mPreviousY;

    /**
     * @param context
     */
    public MyGLSurfaceView(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        this.renderer = new MyGLRenderer(context);
        setRenderer(this.renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        this.renderer.updateMotion(e);
        /*switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = e.getX() + 1f;
                mPreviousY = e.getY() + 1f;
                this.renderer.setJoystickCenter(-(2f * e.getX() / this.getWidth() - 1f), -(2f * e.getY() / this.getHeight() - 1f));
            case MotionEvent.ACTION_MOVE:
                float dx = e.getX() + 1f - mPreviousX;
                float dy = e.getY() + 1f - mPreviousY;
                renderer.updateCameraOrientation(dy * TOUCH_SCALE_FACTOR_MOVE, dx * TOUCH_SCALE_FACTOR_MOVE);
                requestRender();
        }
        mPreviousX = e.getX() + 1f;
        mPreviousY = e.getY() + 1f;*/
        return true;
    }

    public void onPause() {

        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }


}
