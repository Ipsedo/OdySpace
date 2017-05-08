package com.samuelberrien.odyspace.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.levels.Test;
import com.samuelberrien.odyspace.objects.Ship;
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

    private Level currentLevel;
    private Ship ship;
    private Joystick joystick;
    private Controls controls;

    private CollisionThread collisionThread;
    private UpdateThread updateThread;
    private RemoveThread removeThread;

    /**
     * @param context
     */
    public MyGLSurfaceView(Context context, LevelActivity levelActivity, int levelID) {
        super(context);
        this.context = context;
        this.levelActivity = levelActivity;
        this.setEGLContextClientVersion(2);

        this.joystick = new Joystick(this.context);
        this.controls = new Controls(this.context);

        this.currentLevel = new Test();

        this.renderer = new MyGLRenderer(this.context, this, levelID, this.currentLevel, this.joystick, this.controls);
        this.setRenderer(this.renderer);
        this.checkAppResult = new CheckAppResult();
        this.checkAppResult.execute();
        /*this.collisionThread = new CollisionThread();
        this.collisionThread.execute();
        this.updateThread = new UpdateThread();
        this.updateThread.execute();
        this.removeThread = new RemoveThread();
        this.removeThread.execute();*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int pointerIndex = e.getActionIndex();
        float x = -(2f * e.getX(pointerIndex) / this.getWidth() - 1f);
        float y = -(2f * e.getY(pointerIndex) / this.getHeight() - 1f);
        synchronized (this.joystick) {
            synchronized (this.controls) {
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (e.getX(pointerIndex) / this.getHeight() > 1f) {
                            if (!this.controls.isTouchFireButton(x, y)) {
                                this.controls.setBoostVisible(true);
                                this.controls.updateBoostPosition(x, y);
                            }
                        } else {
                            this.joystick.setVisible(true);
                            this.joystick.updatePosition(x, y);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (e.getX(pointerIndex) / this.getHeight() > 1f) {
                            this.controls.setBoostVisible(false);
                        } else {
                            this.joystick.setVisible(false);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (e.getPointerCount() > 1) {
                            if (e.getX(1) / this.getHeight() > 1) {
                                if (!this.controls.isTouchFireButton(-(2f * e.getX(1) / this.getWidth() - 1f), -(2f * e.getY(1) / this.getHeight() - 1f))) {
                                    this.controls.updateBoostStickPosition(-(2f * e.getY(1) / this.getHeight() - 1f));
                                } else {
                                    this.controls.turnOffFire();
                                }
                            } else {
                                this.joystick.updateStickPosition(-(2f * e.getX(1) / this.getWidth() - 1f), -(2f * e.getY(1) / this.getHeight() - 1f));
                            }
                        }
                        if (e.getX(0) / this.getHeight() > 1) {
                            if (!this.controls.isTouchFireButton(-(2f * e.getX(0) / this.getWidth() - 1f), -(2f * e.getY(0) / this.getHeight() - 1f))) {
                                this.controls.updateBoostStickPosition(-(2f * e.getY(0) / this.getHeight() - 1f));
                            } else {
                                this.controls.turnOffFire();
                            }
                        } else {
                            this.joystick.updateStickPosition(-(2f * e.getX(0) / this.getWidth() - 1f), -(2f * e.getY(0) / this.getHeight() - 1f));
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (e.getX(pointerIndex) / this.getHeight() > 1f) {
                            if (!this.controls.isTouchFireButton(x, y)) {
                                this.controls.setBoostVisible(true);
                                this.controls.updateBoostPosition(x, y);
                            }
                        } else {
                            this.joystick.setVisible(true);
                            this.joystick.updatePosition(x, y);
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        if (e.getX(pointerIndex) / this.getHeight() > 1f) {
                            this.controls.setBoostVisible(false);
                        } else {
                            this.joystick.setVisible(false);
                        }
                        break;
                }
            }
        }
        return true;
    }

    public void onPause() {
        this.checkAppResult.cancel(true);
        /*this.collisionThread.cancel(true);
        this.removeThread.cancel(true);
        this.updateThread.cancel(true);*/
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

        /*if(this.updateThread.isCancelled()) {
            this.updateThread.cancel(false);
        }
        if(this.updateThread.getStatus() == AsyncTask.Status.FINISHED) {
            this.updateThread = new UpdateThread();
            this.updateThread.execute();
        }

        if(this.removeThread.isCancelled()) {
            this.removeThread.cancel(false);
        }
        if(this.removeThread.getStatus() == AsyncTask.Status.FINISHED) {
            this.removeThread = new RemoveThread();
            this.removeThread.execute();
        }

        if(this.collisionThread.isCancelled()) {
            this.collisionThread.cancel(false);
        }
        if(this.collisionThread.getStatus() == AsyncTask.Status.FINISHED) {
            this.collisionThread = new CollisionThread();
            this.collisionThread.execute();
        }*/
    }

    private class CheckAppResult extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while(!this.isCancelled()) {
                try {
                    Thread.sleep(1000L / 120L);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                if(MyGLSurfaceView.this.renderer.isDead()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(LevelActivity.LEVEL_RESULT, Integer.toString(0));
                    resultIntent.putExtra(LevelActivity.LEVEL_SCORE, Integer.toString(MyGLSurfaceView.this.renderer.getLevelScore()));
                    MyGLSurfaceView.this.levelActivity.setResult(Activity.RESULT_OK, resultIntent);
                    MyGLSurfaceView.this.levelActivity.finish();
                }
                if(MyGLSurfaceView.this.renderer.isWinner()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(LevelActivity.LEVEL_RESULT, Integer.toString(1));
                    resultIntent.putExtra(LevelActivity.LEVEL_SCORE, Integer.toString(MyGLSurfaceView.this.renderer.getLevelScore()));
                    MyGLSurfaceView.this.levelActivity.setResult(Activity.RESULT_OK, resultIntent);
                    MyGLSurfaceView.this.levelActivity.finish();
                }
            }
            return null;
        }
    }

    private class CollisionThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while(!this.isCancelled()) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                MyGLSurfaceView.this.currentLevel.collide();
            }
            return null;
        }
    }

    private class UpdateThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while(!this.isCancelled() && !MyGLSurfaceView.this.currentLevel.isInit()){

            }
            while(!this.isCancelled()) {
                System.out.println("GO : running");
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                MyGLSurfaceView.this.currentLevel.update(MyGLSurfaceView.this.joystick, MyGLSurfaceView.this.controls);
            }
            return null;
        }
    }

    private class RemoveThread extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            while(!this.isCancelled()) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                MyGLSurfaceView.this.currentLevel.removeObjects();
            }
            return null;
        }
    }
}
