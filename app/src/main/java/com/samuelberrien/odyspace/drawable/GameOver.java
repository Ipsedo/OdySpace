package com.samuelberrien.odyspace.drawable;

import android.content.Context;
import android.opengl.Matrix;

/**
 * Created by samuel on 23/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class GameOver extends ObjModel {
    public GameOver(Context context) {
        super(context, "game_over.obj", 1f, 0f, 0f, 1f, 0f);
    }

    public void draw(float ratio){
        float[] mViewMatrix = new float[16];
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        float[] mVPMatrix = new float[16];
        float[] mPMatrix = new float[16];
        Matrix.orthoM(mPMatrix, 0, -1f * ratio, 1f * ratio, -1f, 1f, -1f, 1f);
        Matrix.multiplyMM(mVPMatrix, 0, mPMatrix, 0, mViewMatrix, 0);
        float[] mMVPMatrix = new float[16];
        float[] mMMatrix = new float[16];
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.translateM(mMMatrix, 0, 0f, 0f, 0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

        super.draw(mMVPMatrix, mVPMatrix, new float[]{0f, 0f, -1f});
    }
}
