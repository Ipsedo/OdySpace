package com.samuelberrien.odyspace.drawable;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by samuel on 17/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Joystick {

    private int nbPoint = 32;
    private float[] mCirclePoint = new float[nbPoint * 3];

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private float[] mPosition = new float[3];

    private float ratio;

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mProgram;

    float color[] = {0.2f, 0.709803922f, 0.898039216f, 1.0f};

    public Joystick(Context context){
        int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.joystick_vs));
        int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.joystick_fs));

        this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(this.mProgram);

        this.makeCricle();
        this.bind();
    }

    private void makeCricle(){
        short[] drawOrder = new short[this.nbPoint];
        for(int i = 0; i < this.nbPoint; i++){
            double mTmpAngle = (double) i * Math.PI * 2d / (double) this.nbPoint;
            this.mCirclePoint[i * 3 + 0] = (float) (0.3d * Math.cos(mTmpAngle));
            this.mCirclePoint[i * 3 + 1] = (float) (0.3d * Math.sin(mTmpAngle));
            this.mCirclePoint[i * 3 + 2] = 0f;
            drawOrder[i] = (short) i;
        }
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                this.mCirclePoint.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(this.mCirclePoint);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
    }

    private void bind(){
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

    }

    public void updatePosition(float x, float y){
        this.mPosition[0] = x * this.ratio;
        this.mPosition[1] = y;
        this.mPosition[2] = 0f;
    }

    public void setRatio(float ratio){
        this.ratio = ratio;
    }

    public void draw() {
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
        Matrix.translateM(mMMatrix, 0, this.mPosition[0], this.mPosition[1], this.mPosition[2]);
        Matrix.multiplyMM(mMVPMatrix, 0, mVPMatrix, 0, mMMatrix, 0);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, 3,
                GLES20.GL_FLOAT, false,
                3 * 4, vertexBuffer);

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, this.nbPoint);

        ShaderLoader.checkGlError("all");
    }
}
