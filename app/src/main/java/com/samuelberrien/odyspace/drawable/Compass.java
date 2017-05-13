package com.samuelberrien.odyspace.drawable;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Ship;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by samuel on 13/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Compass {

    private final float[] triangle = new float[]{0f, 1f, 0f,
            (float) Math.cos(-Math.PI / 3), (float) Math.sin(-Math.PI / 3), 0f,
            (float) Math.cos(-Math.PI * 2 / 3), (float) Math.sin(-Math.PI * 2 / 3), 0f};

    private FloatBuffer triangleBuffer;

    private float[] color = {236f / 255f, 240f / 255f, 241f / 255f, 1f};

    private float[] mModelMatrix;

    private double angleWithFrontVec;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mProgram;

    public Compass(Context context) {
        int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.simple_vs));
        int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.simple_fs));

        this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(this.mProgram);
        this.bind();
        this.makeTriangle();

        this.mModelMatrix = new float[16];
        this.angleWithFrontVec = 0f;
    }

    private void makeTriangle() {
        ByteBuffer bb = ByteBuffer.allocateDirect(this.triangle.length * 4);
        bb.order(ByteOrder.nativeOrder());
        this.triangleBuffer = bb.asFloatBuffer();
        this.triangleBuffer.put(this.triangle);
        this.triangleBuffer.position(0);
    }

    private void bind() {
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void update(Ship from, BaseItem to) {
        float[] vecShipToOther = from.fromCamTo(to); //from.vector3fTo(to);
        float[] vecUpShip = from.getCamUpVec();
        float[] vecFrontShip = Vector.normalize3f(from.getCamLookAtVec());

        float[] vecProjeté = Vector.cross3f(vecFrontShip, Vector.cross3f(vecShipToOther, vecFrontShip));

        this.angleWithFrontVec = Math.acos(Vector.dot3f(Vector.normalize3f(vecFrontShip), Vector.normalize3f(vecShipToOther)));

        double angle = Math.acos(Vector.dot3f(Vector.normalize3f(vecUpShip), Vector.normalize3f(vecProjeté)));

        float[] vecDansRepereShip = Vector.normalize3f(from.invVecWithRotMatrix(vecProjeté));

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        float x = vecDansRepereShip[0] >= 1f ? 0.9f : vecDansRepereShip[0] <= -1f ? -0.9f : vecDansRepereShip[0];
        float y = vecDansRepereShip[1] >= 1f ? 0.9f : vecDansRepereShip[1] <= -1f ? -0.9f : vecDansRepereShip[1];
        Matrix.translateM(mModelMatrix, 0, x, y, 0f);
        float[] rotMat = new float[16];
        Matrix.setRotateM(rotMat, 0, (float) Math.toDegrees(angle), 0f, 0f, 1f);
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, rotMat, 0);
        Matrix.scaleM(mModelMatrix, 0, 0.1f, 0.1f, 0.1f);

        this.mModelMatrix = mModelMatrix.clone();
    }

    public void draw(float ratio) {
        if (this.angleWithFrontVec > Math.toRadians(30d)) {
            float[] mViewMatrix = new float[16];
            Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            float[] mVPMatrix = new float[16];
            float[] mPMatrix = new float[16];
            Matrix.orthoM(mPMatrix, 0, -1f * ratio, 1f * ratio, -1f, 1f, -1f, 1f);
            Matrix.multiplyMM(mVPMatrix, 0, mPMatrix, 0, mViewMatrix, 0);
            float[] mMVPMatrix = new float[16];
            Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, this.mModelMatrix, 0);

            GLES20.glEnableVertexAttribArray(this.mPositionHandle);
            GLES20.glVertexAttribPointer(this.mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.triangleBuffer);
            GLES20.glUniform4fv(this.mColorHandle, 1, this.color, 0);
            GLES20.glUniformMatrix4fv(this.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.triangle.length / 3);
        }
    }
}
