package com.samuelberrien.odyspace.game;

/**
 * Created by samuel on 16/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;

import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.text.GameOver;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.text.LevelDone;
import com.samuelberrien.odyspace.levels.Test;
import com.samuelberrien.odyspace.levels.TestBoss;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.game.Level;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Context context;

    private MyGLSurfaceView myGLSurfaceView;

    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] mLightPosInEyeSpace = new float[4];
    private final float[] mLightModelMatrix = new float[16];
    private final float[] mLightPosInWorldSpace = new float[4];

    private float[] mCameraPosition = new float[3];
    private float[] mCameraDirection = new float[3];
    private float[] mCameraUpVec = new float[3];
    private float phi = 0f;
    private float theta = 0f;
    private float maxRange = 1f;
    private float projectionAngle = 40f;
    private float maxProjDist = 300f;
    private float ratio = 1f;
    private int width;
    private int height;

    private Joystick joystick;
    private Controls controls;

    private int currLevelId;
    private Level currentLevel;
    private boolean willQuit;
    private boolean isWinner;
    private boolean isDead;

    private Ship ship;

    private long currTime = System.currentTimeMillis();

    /**
     * @param context
     */
    public MyGLRenderer(Context context, MyGLSurfaceView myGLSurfaceView, int currLevelId) {
        this.context = context;
        this.myGLSurfaceView = myGLSurfaceView;
        this.currLevelId = currLevelId;
        this.willQuit = false;
        this.isWinner = false;
        this.isDead = false;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.mCameraDirection = new float[]{this.mCameraPosition[0], this.mCameraPosition[1], this.mCameraPosition[2] + 1f};

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);
        GLES20.glClearColor(0.1f, 0.0f, 0.3f, 1.0f);

        this.joystick = new Joystick(this.context);
        this.controls = new Controls(this.context);

        this.ship = new Ship(this.context);

        this.mCameraPosition = new float[]{0f, 0f, -10f};
        this.mCameraUpVec = new float[]{0f, 1f, 0f};
        this.currentLevel = this.getCurrentLevel(this.currLevelId);

        this.currentLevel.init(this.context, this.ship, 1000f);

        this.updateCameraPosition(this.ship.getCamPosition());
        this.updateCamLookVec(this.ship.getCamLookAtVec());
        this.updateCamUpVec(this.ship.getCamUpVec());
    }

    private Level getCurrentLevel(int currLevelId){
        if(currLevelId == 0){
            return new Test();
        } else {
            return new TestBoss();
        }
    }

    private void levelUp(){
        this.currLevelId++;
        this.currentLevel = this.getCurrentLevel(this.currLevelId);
        this.currentLevel.init(this.context, this.ship, 1000f);
    }

    public int getLevelScore() {
        return this.currentLevel.getScore();
    }

    /**
     * update the camera look point with orientation angles
     *
     * @param phi   angle phi
     * @param theta angle theta
     */
    protected void updateCameraOrientation(float phi, float theta) {
        this.phi += phi;
        this.theta += theta;

        if (this.phi > Math.PI * 2) {
            this.phi -= Math.PI * 2;
        }
        if (this.phi < 0) {
            this.phi += Math.PI * 2;
        }
        if (this.theta > Math.PI * 2) {
            this.theta -= Math.PI * 2;
        }
        if (this.theta < 0) {
            this.theta += Math.PI * 2;
        }
        if ((this.phi > Math.toRadians(80) && this.phi < Math.toRadians(100)) || (this.phi > Math.toRadians(260) && this.phi < Math.toRadians(280))) {
            this.phi -= phi * 2;
        }

        this.mCameraDirection[0] = this.maxRange * (float) (Math.cos(this.phi) * Math.sin(this.theta)) + this.mCameraPosition[0];
        this.mCameraDirection[1] = this.maxRange * (float) Math.sin(this.phi) + this.mCameraPosition[1];
        this.mCameraDirection[2] = this.maxRange * (float) (Math.cos(this.phi) * Math.cos(this.theta)) + this.mCameraPosition[2];
    }

    /**
     * Update the camera look at vector (normalized)
     * @param xyz The x y z vector
     */
    private void updateCamLookVec(float[] xyz){
        this.mCameraDirection[0] = this.maxRange * xyz[0] + this.mCameraPosition[0];
        this.mCameraDirection[1] = this.maxRange * xyz[1] + this.mCameraPosition[1];
        this.mCameraDirection[2] = this.maxRange * xyz[2] + this.mCameraPosition[2];
    }

    /**
     * Update the camera up vector
     * @param xyz The x y z vector
     */
    private void updateCamUpVec(float[] xyz){
        this.mCameraUpVec[0] = xyz[0];
        this.mCameraUpVec[1] = xyz[1];
        this.mCameraUpVec[2] = xyz[2];
    }

    /**
     * Update the camera position
     *
     * @param mCameraPosition A 3D vector contening x, y and z new camera position
     */
    private void updateCameraPosition(float[] mCameraPosition){
        this.mCameraPosition = mCameraPosition;
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    private void updateLight(float x, float y, float z) {
        Matrix.setIdentityM(this.mLightModelMatrix, 0);
        Matrix.translateM(this.mLightModelMatrix, 0, x, y, z);
        Matrix.multiplyMV(this.mLightPosInWorldSpace, 0, this.mLightModelMatrix, 0, this.mLightPosInModelSpace, 0);
        Matrix.multiplyMV(this.mLightPosInEyeSpace, 0, this.mViewMatrix, 0, this.mLightPosInWorldSpace, 0);
    }

    /**
     *
     * @param e
     */
    public void updateMotion(MotionEvent e){
        int pointerIndex = e.getActionIndex();
        float x = -(2f * e.getX(pointerIndex) / this.width - 1f);
        float y = -(2f * e.getY(pointerIndex) / this.height - 1f);
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if(e.getX(pointerIndex) / this.height > 1f){
                    if(!this.controls.isTouchFireButton(x, y)) {
                        this.controls.setBoostVisible(true);
                        this.controls.updateBoostPosition(x, y);
                    }
                } else {
                    this.joystick.setVisible(true);
                    this.joystick.updatePosition(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(e.getX(pointerIndex) / this.height > 1f){
                    this.controls.setBoostVisible(false);
                } else {
                    this.joystick.setVisible(false);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(e.getPointerCount() > 1) {
                    if(e.getX(1) / this.height > 1) {
                        if(!this.controls.isTouchFireButton(-(2f * e.getX(1) / this.width - 1f), -(2f * e.getY(1) / this.height - 1f))) {
                            this.controls.updateBoostStickPosition(-(2f * e.getY(1) / this.height - 1f));
                        } else {
                            this.controls.turnOffFire();
                        }
                    } else {
                        this.joystick.updateStickPosition(-(2f * e.getX(1) / this.width - 1f), -(2f * e.getY(1) / this.height - 1f));
                    }
                }
                if(e.getX(0) / this.height > 1) {
                    if(!this.controls.isTouchFireButton(-(2f * e.getX(0) / this.width - 1f), -(2f * e.getY(0) / this.height - 1f))) {
                        this.controls.updateBoostStickPosition(-(2f * e.getY(0) / this.height - 1f));
                    } else {
                        this.controls.turnOffFire();
                    }
                } else {
                    this.joystick.updateStickPosition(-(2f * e.getX(0) / this.width - 1f), -(2f * e.getY(0) / this.height - 1f));
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if(e.getX(pointerIndex) / this.height > 1f){
                    if(!this.controls.isTouchFireButton(x, y)) {
                        this.controls.setBoostVisible(true);
                        this.controls.updateBoostPosition(x, y);
                    }
                } else {
                    this.joystick.setVisible(true);
                    this.joystick.updatePosition(x, y);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if(e.getX(pointerIndex) / this.height > 1f){
                    this.controls.setBoostVisible(false);
                } else {
                    this.joystick.setVisible(false);
                }
                break;
        }
    }

    public boolean isWinner(){
        return this.isWinner;
    }

    public boolean isDead(){
        return this.isDead;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        if(this.willQuit){
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            if(this.currentLevel.isWinner()){
                this.isWinner = true;
            } else if(this.currentLevel.isDead()){
                this.isDead = true;
            }
            this.willQuit = false;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, this.ratio, 1, this.maxProjDist);
        Matrix.setLookAtM(this.mViewMatrix, 0, this.mCameraPosition[0], this.mCameraPosition[1], this.mCameraPosition[2], this.mCameraDirection[0], this.mCameraDirection[1], this.mCameraDirection[2], this.mCameraUpVec[0], this.mCameraUpVec[1], this.mCameraUpVec[2]);

        this.updateLight(0f, 250f, 0f);

        this.currentLevel.update(this.joystick, this.controls);
        this.currentLevel.removeObjects();

        this.updateCameraPosition(this.ship.getCamPosition());
        this.updateCamLookVec(this.ship.getCamLookAtVec());
        this.updateCamUpVec(this.ship.getCamUpVec());

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        this.currentLevel.draw(this.mProjectionMatrix.clone(), this.mViewMatrix.clone(), this.mLightPosInEyeSpace.clone(), this.mCameraPosition.clone());

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        this.joystick.draw();
        this.controls.draw();
        this.ship.drawLife();

        if(this.currentLevel.isDead()){
            new GameOver(this.context).draw(this.ratio);
            this.willQuit = true;
        }else if(this.currentLevel.isWinner()){
            new LevelDone(this.context).draw(this.ratio);
            this.willQuit = true;
        }

        System.out.println("FPS : " + 1000L / (System.currentTimeMillis() - this.currTime));
        this.currTime = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;

        this.ratio = (float) width / height;

        this.joystick.setRatio(this.ratio);
        this.controls.setRatio(this.ratio);
        this.ship.setRatio(this.ratio);

        //Matrix.frustumM(mProjectionMatrix, 0, -this.ratio, this.ratio, -1, 1, 3, 50f);
        Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, ratio, 1, this.maxProjDist);
    }
}
