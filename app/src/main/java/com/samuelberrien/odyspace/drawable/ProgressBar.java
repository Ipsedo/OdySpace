package com.samuelberrien.odyspace.drawable;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * Created by samuel on 11/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ProgressBar {

    private float[] mModelMatrix;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mProgram;

    public void draw(float ratio) {
        float[] mViewMatrix = new float[16];
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        float[] mVPMatrix = new float[16];
        float[] mPMatrix = new float[16];
        Matrix.orthoM(mPMatrix, 0, -1f * ratio, 1f * ratio, -1f, 1f, -1f, 1f);
        Matrix.multiplyMM(mVPMatrix, 0, mPMatrix, 0, mViewMatrix, 0);
        float[] mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, this.mModelMatrix, 0);
    }
}
