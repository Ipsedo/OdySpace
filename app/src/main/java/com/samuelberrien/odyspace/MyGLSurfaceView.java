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

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.renderer.setJoystickCenter(-(2f * e.getX() / this.getWidth() - 1f), -(2f * e.getY() / this.getHeight() - 1f));
            case MotionEvent.ACTION_MOVE:
                requestRender();
        }
        return true;
    }

    public void onPause() {

        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }


}
