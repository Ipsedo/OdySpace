package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtl;
import com.samuelberrien.odyspace.utils.game.Fire;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Ship extends BaseItem {

    private Context context;

    public static float MAX_SPEED = 0.0125f;
    private float maxSpeed = Ship.MAX_SPEED;
    private final float rollCoeff = 1f;
    private final float pitchCoeff = 0.5f;

    private final float[] originalSpeedVec = new float[]{0f, 0f, 1f, 0f};
    private final float[] originalUpVec = new float[]{0f, 1f, 0f, 0f};

    public static int MAXLIFE = 25;
    private Life lifeDraw;

    private Explosion mExplosion;
    private boolean exploded;

    private ObjModelMtl rocket;

    private Fire.Type fireType;

    public Ship(Context context) {
        super(context, "ship.obj", "ship.mtl", 1f, 0f, Ship.MAXLIFE, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 1f}, new float[]{0f, 0f, 0f}, 1f);
        this.context = context;
        this.lifeDraw = new Life(this.context);
        this.rocket = new ObjModelMtl(this.context, "rocket.obj", "rocket.mtl", 2f, 0f);
        this.setFireType();
        this.exploded = false;
    }

    public void addExplosion(List<Explosion> explosions) {
        if(!this.exploded) {
            this.mExplosion.setPosition(this.mPosition.clone());
            explosions.add(this.mExplosion);
            this.exploded = true;
        }
    }

    public void makeExplosion() {
        this.mExplosion = new Explosion(context, super.mPosition.clone(), super.allDiffColorBuffer, 0.5f, 0.16f);
    }

    private void setFireType() {
        SharedPreferences sharedPref = this.context.getSharedPreferences(this.context.getString(R.string.saved_ship_info), Context.MODE_PRIVATE);
        String defaultValue = this.context.getString(R.string.saved_fire_type_default);
        String fireType = sharedPref.getString(this.context.getString(R.string.current_fire_type), defaultValue);
        if (fireType.equals(this.context.getString(R.string.fire_bonus_1))) {
            this.fireType = Fire.Type.SIMPLE_FIRE;
        } else if (fireType.equals(this.context.getString(R.string.fire_bonus_2))) {
            this.fireType = Fire.Type.QUINT_FIRE;
        } else if (fireType.equals(this.context.getString(R.string.fire_bonus_3))) {
            this.fireType = Fire.Type.SIMPLE_BOMB;
        }
    }

    public void move(Joystick joystick, Controls controls) {
        this.maxSpeed = this.MAX_SPEED * (float) Math.pow((controls.getBoost() + 2f) * 2f, 2d);

        float[] tmp = joystick.getStickPosition();

        float phi = tmp[0];
        float theta = tmp[1];

        float[] pitchMatrix = new float[16];
        float[] rollMatrix = new float[16];
        Matrix.setRotateM(rollMatrix, 0, (phi >= 0 ? (float) Math.exp(phi) - 1f : (float) -Math.exp(Math.abs(phi)) + 1f) * this.rollCoeff, 0f, 0f, 1f);
        Matrix.setRotateM(pitchMatrix, 0, (theta >= 0 ? (float) Math.exp(theta) - 1f : (float) -Math.exp(Math.abs(theta)) + 1f) * this.pitchCoeff, 1f, 0f, 0f);

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

    public void fire(Controls controls, List<BaseItem> rockets) {
        if (controls.isFire()) {
            Fire.fire(this.rocket, rockets, this.fireType, super.mPosition.clone(), super.mSpeed.clone(), super.mRotationMatrix.clone(), this.maxSpeed);
            controls.turnOffFire();
        }
    }

    public float[] fromCamTo(BaseItem to) {
        float[] camPos = this.getCamPosition();
        return new float[]{to.mPosition[0] - camPos[0], to.mPosition[1] - camPos[1], to.mPosition[2] - camPos[2]};
    }

    public float[] getCamFrontVec() {
        float[] lookAtPos = new float[3];
        float[] u = new float[4];
        Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalSpeedVec, 0);

        lookAtPos[0] = u[0] + this.mPosition[0];
        lookAtPos[1] = u[1] + this.mPosition[1];
        lookAtPos[2] = u[2] + this.mPosition[2];

        float[] camPos = this.getCamPosition();

        return new float[] {lookAtPos[0] - camPos[0], lookAtPos[1] - camPos[1], lookAtPos[2] - camPos[2]};
    }

    public float[] getCamPosition() {
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

    public float[] getCamLookAtVec() {
        float[] res = new float[3];
        float[] u = new float[4];
        Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalSpeedVec, 0);

        res[0] = u[0];
        res[1] = u[1];
        res[2] = u[2];

        return res;
    }

    public float[] getCamUpVec() {
        float[] res = new float[3];
        float[] u = new float[4];
        Matrix.multiplyMV(u, 0, super.mRotationMatrix, 0, this.originalUpVec, 0);

        res[0] = u[0];
        res[1] = u[1];
        res[2] = u[2];

        return res;
    }

    public float[] invVecWithRotMatrix(float[] vec) {
        float[] tmp = new float[] {vec[0], vec[1], vec[2], 0f};
        float[] invModel = new float[16];
        Matrix.invertM(invModel, 0, this.mRotationMatrix, 0);

        float[] tmpRes = new float[4];
        Matrix.multiplyMV(tmpRes, 0, invModel, 0, tmp, 0);

        return new float[] {tmpRes[0], tmpRes[1], tmpRes[2]};
    }

    public void updateMaxSpeed(float coeff) {

    }

    @Override
    public void move() {

    }

    public void drawLife(float ratio) {
        this.lifeDraw.draw(ratio);
    }

    private class Life {

        private FloatBuffer lifeContainer;
        private FloatBuffer life;

        private int mPositionHandle;
        private int mColorHandle;
        private int mMVPMatrixHandle;
        private int mProgram;

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
        }

        private void makeLifeContainer() {
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

        private void makeLife() {
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

        private void bind() {
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        }

        public void draw(float ratio) {
            GLES20.glUseProgram(this.mProgram);

            float[] mViewMatrix = new float[16];
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            float[] mVPMatrix = new float[16];
            float[] mPMatrix = new float[16];
            Matrix.orthoM(mPMatrix, 0, -1f * ratio, 1f * ratio, -1f, 1f, -1f, 1f);
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
