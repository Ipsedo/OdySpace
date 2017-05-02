package com.samuelberrien.odyspace;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.samuelberrien.odyspace.utils.game.Level;

/**
 * Created by samuel on 16/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private Context context;
    private LevelActivity levelActivity;

    private MyGLRenderer renderer;

    private CheckAppResult checkAppResult;

    /**
     * @param context
     */
    public MyGLSurfaceView(Context context, LevelActivity levelActivity) {
        super(context);
        this.context = context;
        this.levelActivity = levelActivity;
        // Create an OpenGL ES 2.0 context.
        this.setEGLContextClientVersion(2);


        this.renderer = new MyGLRenderer(context, this, 0);
        this.setRenderer(this.renderer);
        this.checkAppResult = new CheckAppResult();
        this.checkAppResult.execute();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        this.renderer.updateMotion(e);
        return true;
    }

    public void onPause() {
        this.checkAppResult.cancel(true);
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        if(this.checkAppResult.isCancelled()) {
            this.checkAppResult.cancel(false);
        }
        if (this.checkAppResult.getStatus() == AsyncTask.Status.FINISHED) {
            this.checkAppResult = new CheckAppResult();
            this.checkAppResult.execute();
        }
    }

    private class CheckAppResult extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while(!this.isCancelled()) {
                if(MyGLSurfaceView.this.renderer.isWinner()) {
                    MyGLSurfaceView.this.levelActivity.finish();
                }
                try {
                    Thread.sleep(1000L / 120L);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            return null;
        }
    }
}
