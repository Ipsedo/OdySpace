package com.samuelberrien.odyspace.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.levels.TestBossThread;
import com.samuelberrien.odyspace.levels.TestThread;
import com.samuelberrien.odyspace.utils.game.threads.CollisionThread;
import com.samuelberrien.odyspace.utils.game.threads.EndGameThread;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.game.threads.RemoveThread;
import com.samuelberrien.odyspace.utils.game.threads.UpdateThread;

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

    private Level currentLevel;
    private Joystick joystick;
    private Controls controls;

    private CollisionThread collisionThread;
    private UpdateThread updateThread;
    private RemoveThread removeThread;
    private EndGameThread endGameThread;

    /**
     * @param context
     * @param levelActivity
     * @param levelID
     */
    public MyGLSurfaceView(Context context, LevelActivity levelActivity, int levelID) {
        super(context);
        this.context = context;
        this.levelActivity = levelActivity;
        this.setEGLContextClientVersion(2);

        this.joystick = new Joystick(this.context);
        this.controls = new Controls(this.context);

        this.currentLevel = this.getCurrentLevel(levelID);

        this.renderer = new MyGLRenderer(this.context, this, this.currentLevel, this.joystick, this.controls);
        this.setRenderer(this.renderer);
    }

    /**
     * @param currLevelId
     * @return
     */
    private Level getCurrentLevel(int currLevelId) {
        if (currLevelId == 0) {
            return new TestThread();
        } else {
            return new TestBossThread();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.initThreads();
    }

    @Override
    public void onDetachedFromWindow() {
        this.killThread();
        super.onDetachedFromWindow();
    }

    private void initThreads() {
        this.collisionThread = new CollisionThread(this.currentLevel);
        this.updateThread = new UpdateThread(this.currentLevel);
        this.removeThread = new RemoveThread(this.currentLevel);
        this.endGameThread = new EndGameThread(this.currentLevel, this.levelActivity);
        this.collisionThread.start();
        this.updateThread.start();
        this.removeThread.start();
        this.endGameThread.start();
    }

    private void killThread() {
        this.collisionThread.setCanceled(true);
        this.updateThread.setCanceled(true);
        this.removeThread.setCanceled(true);
        this.endGameThread.setCanceled(true);
        try {
            this.collisionThread.join();
            this.updateThread.join();
            this.removeThread.join();
            this.endGameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int pointerIndex = e.getActionIndex();
        float x = -(2f * e.getX(pointerIndex) / this.getWidth() - 1f);
        float y = -(2f * e.getY(pointerIndex) / this.getHeight() - 1f);
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
        return true;
    }
}
