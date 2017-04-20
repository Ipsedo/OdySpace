package com.samuelberrien.odyspace;

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

import com.samuelberrien.odyspace.drawable.HeightMap;
import com.samuelberrien.odyspace.drawable.Joystick;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.Level;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    protected Context context;

    protected final float[] mProjectionMatrix = new float[16];
    protected final float[] mViewMatrix = new float[16];

    private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    protected final float[] mLightPosInEyeSpace = new float[4];
    private final float[] mLightModelMatrix = new float[16];
    private final float[] mLightPosInWorldSpace = new float[4];

    protected float[] mCameraPosition = new float[3];
    protected float[] mCameraDirection = new float[3];
    protected float[] mCameraUpVec = new float[3];
    private float phi = 0f;
    private float theta = 0f;
    private float maxRange = 1f;
    private float projectionAngle = 40f;
    private float ratio = 1f;
    private int width;
    private int height;

    private Joystick joystick;

    private Level currentLevel;

    private HeightMap heightMap;

    private Ship ship;

    /**
     * @param context
     */
    public MyGLRenderer(Context context) {
        this.context = context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.mCameraDirection = new float[]{this.mCameraPosition[0], this.mCameraPosition[1], this.mCameraPosition[2] + 1f};
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);
        GLES20.glClearColor(0.1f, 0.0f, 0.3f, 1.0f);
        this.joystick = new Joystick(this.context);
        this.ship = new Ship(this.context);
        this.heightMap = new HeightMap(context, R.drawable.canyon_6_hm_2, R.drawable.canyon_6_tex_2, 0.025f, 0.8f, 3e-5f, 100f);
        this.mCameraPosition = new float[]{0f, 0f, -10f};
        this.mCameraUpVec = new float[]{0f, 1f, 0f};
    }

    /**
     * update the camera look point with orientation angles
     *
     * @param phi   angle phi
     * @param theta angle theta
     */
    public void updateCameraOrientation(float phi, float theta) {
        this.phi = phi;
        this.theta = theta;

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
    public void updateCamLookVec(float[] xyz){
        this.mCameraDirection[0] = this.maxRange * xyz[0] + this.mCameraPosition[0];
        this.mCameraDirection[1] = this.maxRange * xyz[1] + this.mCameraPosition[1];
        this.mCameraDirection[2] = this.maxRange * xyz[2] + this.mCameraPosition[2];
    }

    public void updateCamUpVec(float[] xyz){
        this.mCameraUpVec[0] = xyz[0];
        this.mCameraUpVec[1] = xyz[1];
        this.mCameraUpVec[2] = xyz[2];
    }

    /**
     * Update the camera position
     *
     * @param mCameraPosition A 3D vector contening x, y and z new camera position
     */
    public void updateCameraPosition(float[] mCameraPosition){
        this.mCameraPosition = mCameraPosition;
    }

    /**
     * @param x
     * @param y
     * @param z
     */
    protected void updateLight(float x, float y, float z) {
        Matrix.setIdentityM(this.mLightModelMatrix, 0);
        Matrix.translateM(this.mLightModelMatrix, 0, x, y, z);
        Matrix.multiplyMV(this.mLightPosInWorldSpace, 0, this.mLightModelMatrix, 0, this.mLightPosInModelSpace, 0);
        Matrix.multiplyMV(this.mLightPosInEyeSpace, 0, this.mViewMatrix, 0, this.mLightPosInWorldSpace, 0);
    }

    public void updateMotion(MotionEvent e){
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.joystick.setVisible(true);
                this.joystick.updatePosition(-(2f * e.getX() / this.width - 1f), -(2f * e.getY() / this.height - 1f));
                break;
            case MotionEvent.ACTION_MOVE:
                this.joystick.updateStickPosition(-(2f * e.getX() / this.width - 1f), -(2f * e.getY() / this.height - 1f));
                break;
            case MotionEvent.ACTION_UP:
                this.joystick.setVisible(false);
                break;
        }
    }



    public void onDrawFrame(GL10 unused) {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        this.updateProjection();
        // Set the camera position (View matrix)
        Matrix.setLookAtM(this.mViewMatrix, 0, this.mCameraPosition[0], this.mCameraPosition[1], this.mCameraPosition[2], this.mCameraDirection[0], this.mCameraDirection[1], this.mCameraDirection[2], this.mCameraUpVec[0], this.mCameraUpVec[1], this.mCameraUpVec[2]);

        float[] tmp = this.joystick.getStickPosition();
        this.ship.move(tmp[0], tmp[1]);

        this.updateCameraPosition(this.ship.getCamPosition());
        //float[] phiTheta = this.ship.getPhiTheta();
        this.updateCamLookVec(this.ship.getCamLookAtVec());
        this.updateCamUpVec(this.ship.getCamUpVec());

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        this.ship.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, this.mCameraPosition);
        this.heightMap.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace);

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        this.joystick.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;

        this.ratio = (float) width / height;

        this.joystick.setRatio(this.ratio);

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        //Matrix.frustumM(mProjectionMatrix, 0, -this.ratio, this.ratio, -1, 1, 3, 50f);

        Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, ratio, 1, 50f);
    }

    private void updateProjection() {
        Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, this.ratio, 1, 50f);
    }
}
