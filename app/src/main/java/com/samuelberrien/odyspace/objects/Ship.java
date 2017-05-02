package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Ship extends BaseItem {

    private Context context;

    private final float originalMaxSpeed = 0.025f;
    private float maxSpeed = this.originalMaxSpeed;
    private final float rollCoeff = 2f;
    private final float pitchCoeff = 1f;

    private final float[] originalSpeedVec = new float[]{0f, 0f, 1f, 0f};
    private final float[] originalUpVec = new float[]{0f, 1f, 0f, 0f};

    private final int MAXLIFE = 25;
    private Life lifeDraw;

    public Ship(Context context){
        super(context, "ship.obj", "ship.mtl", 1f, 0f, 1, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 1f}, new float[]{0f, 0f, 0f});
        this.life = this.MAXLIFE;
        this.context = context;
        this.lifeDraw = new Life(this.context);
    }

    public void move(float phi, float theta){
        float[] pitchMatrix = new float[16];
        float[] rollMatrix = new float[16];
        Matrix.setRotateM(rollMatrix, 0, phi * this.rollCoeff, 0f, 0f, 1f);
        Matrix.setRotateM(pitchMatrix, 0, theta * this.pitchCoeff, 1f, 0f, 0f);

        float[] currRotMatrix = new float[16];
        Matrix.multiplyMM(currRotMatrix, 0, pitchMatrix, 0, rollMatrix, 0);

        float[] currSpeed = new float[4];
        Matrix.multiplyMV(currSpeed, 0, currRotMatrix, 0, this.originalSpeedVec, 0);

        float[] realSpeed = new float[4];
        Matrix.multiplyMV(realSpeed, 0, super.mRotationMatrix, 0, currSpeed, 0);

        float[] tmpMat = super.mRotationMatrix.clone();
        Matrix.multiplyMM(super.mRotationMatrix, 0, tmpMat, 0, currRotMatrix, 0);

        super.mPosition[0] += this.maxSpeed * realSpeed[0];
        super.mPosition[1] += this.maxSpeed * realSpeed[1];
        super.mPosition[2] += this.maxSpeed * realSpeed[2];

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
        tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);

        super.mModelMatrix = mModelMatrix;
    }

    public void fire(ArrayList<BaseItem> rockets){
        Rocket tmp = new Rocket(this.context, 2f, super.mPosition.clone(), super.mSpeed.clone(), super.mAcceleration.clone(), super.mRotationMatrix.clone(), this.maxSpeed);
        tmp.move();
        rockets.add(tmp);
    }

    public float[] getCamPosition(){
        float[] res = new float[3];
        float[] u = new float[4];
        Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalSpeedVec, 0);

        float[] v = new float[4];
        Matrix.multiplyMV(v, 0, super.mRotationMatrix, 0, this.originalUpVec, 0);

        res[0] = -10f * u[0] + super.mPosition[0] + 3f * v[0];
        res[1] = -10f * u[1] + super.mPosition[1] + 3f * v[1];
        res[2] = -10f * u[2] + super.mPosition[2] + 3f * v[2];

        return res;
    }

    public float[] getCamLookAtVec(){
        float[] res = new float[3];
        float[] u = new float[4];
        Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalSpeedVec, 0);

        res[0] = u[0];
        res[1] = u[1];
        res[2] = u[2];

        return res;
    }

    public float[] getCamUpVec(){
        float[] res = new float[3];
        float[] u = new float[4];
        Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalUpVec, 0);

        res[0] = u[0];
        res[1] = u[1];
        res[2] = u[2];

        return res;
    }

    public void updateMaxSpeed(float coeff){
        this.maxSpeed = this.originalMaxSpeed * (float) Math.pow((coeff + 2f) * 2f, 2d);
    }

    @Override
    public void move(){

    }

    public void drawLife() {
        this.lifeDraw.draw();
    }

    public void setRatio(float ratio){
        this.lifeDraw.ratio = ratio;
    }

    private class Life {

        private FloatBuffer lifeContainer;
        private FloatBuffer life;

        private int mPositionHandle;
        private int mColorHandle;
        private int mMVPMatrixHandle;
        private int mProgram;

        private float color[] = {0.2f, 0.709803922f, 0.898039216f, 1.0f};

        private float ratio;

        public Life(Context context) {
            int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.simple_vs));
            int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.simple_fs));
            this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
            GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
            GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
            GLES20.glLinkProgram(this.mProgram);
            this.makeLifeContainer();
            this.makeLife();
            this.bind();
            this.ratio = 1f;
        }

        private void makeLifeContainer(){
            float[] mPoints = new float[3 * 4];
            mPoints[0] = -1f;
            mPoints[1] = -1f;
            mPoints[2] = 0f;

            mPoints[3] = -1f;
            mPoints[4] = 1f;
            mPoints[5] = 0f;

            mPoints[6] = 1f;
            mPoints[7] = 1f;
            mPoints[8] = 0f;

            mPoints[9] = 1f;
            mPoints[10] = -1f;
            mPoints[11] = 0f;

            ByteBuffer bb = ByteBuffer.allocateDirect(mPoints.length * 4);
            bb.order(ByteOrder.nativeOrder());
            this.lifeContainer = bb.asFloatBuffer();
            this.lifeContainer.put(mPoints);
            this.lifeContainer.position(0);
        }

        private void makeLife(){
            float[] mPoints = new float[3 * 4];
            mPoints[0] = -1f;
            mPoints[1] = -1f;
            mPoints[2] = 0f;

            mPoints[3] = -1f;
            mPoints[4] = 1f;
            mPoints[5] = 0f;

            mPoints[6] = 1f;
            mPoints[7] = -1f;
            mPoints[8] = 0f;

            mPoints[9] = 1f;
            mPoints[10] = 1f;
            mPoints[11] = 0f;

            ByteBuffer bb = ByteBuffer.allocateDirect(mPoints.length * 4);
            bb.order(ByteOrder.nativeOrder());
            this.life = bb.asFloatBuffer();
            this.life.put(mPoints);
            this.life.position(0);
        }

        private void bind(){
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        }

        public void draw(){
            GLES20.glUseProgram(this.mProgram);

            float[] mViewMatrix = new float[16];
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            float[] mVPMatrix = new float[16];
            float[] mPMatrix = new float[16];
            Matrix.orthoM(mPMatrix, 0, -1f * this.ratio, 1f * this.ratio, -1f, 1f, -1f, 1f);
            Matrix.multiplyMM(mVPMatrix, 0, mPMatrix, 0, mViewMatrix, 0);
            float[] mMVPMatrix = new float[16];

            float[] mMMatrix = new float[16];
            Matrix.setIdentityM(mMMatrix, 0);
            Matrix.translateM(mMMatrix, 0, 0.9f, 0.9f, 0f);
            Matrix.scaleM(mMMatrix, 0, 0.501f, 0.051f, 0.051f);
            Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.lifeContainer);
            GLES20.glUniform4fv(mColorHandle, 1, new float[]{0.2f, 0.709803922f, 0.898039216f, 1.0f}, 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 4);

            Matrix.setIdentityM(mMMatrix, 0);
            Matrix.translateM(mMMatrix, 0, 0.9f + 0.45f * (Ship.this.MAXLIFE - Ship.this.life) / Ship.this.MAXLIFE, 0.9f, 0f);
            Matrix.scaleM(mMMatrix, 0, 0.5f * Ship.this.life / Ship.this.MAXLIFE, 0.050f, 0.05f);
            Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.life);
            GLES20.glUniform4fv(mColorHandle, 1, new float[]{0.8f, 0.2f, 0.1f, 1.0f}, 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }
}
