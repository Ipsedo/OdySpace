package com.samuelberrien.odyspace.drawable.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.BitmapLoader;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by samuel on 11/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class CubeMap {

    private Context context;

    private int mProgram;
    private int texCoordHandle;
    private int mvpMatrixHandle;
    private int samplerCubeHandle;

    private float levelLimits;

    private int[] textureCubeID = new int[1];

    private float[] pointsCubeMap = {
            -1f, 1f, -1f,
            -1f, -1f, -1f,
            1f, -1f, -1f,
            1f, -1f, -1f,
            1f, 1f, -1f,
            -1f, 1f, -1f,

            -1f, -1f, 1f,
            -1f, -1f, -1f,
            -1f, 1f, -1f,
            -1f, 1f, -1f,
            -1f, 1f, 1f,
            -1f, -1f, 1f,

            1f, -1f, -1f,
            1f, -1f, 1f,
            1f, 1f, 1f,
            1f, 1f, 1f,
            1f, 1f, -1f,
            1f, -1f, -1f,

            -1f, -1f, 1f,
            -1f, 1f, 1f,
            1f, 1f, 1f,
            1f, 1f, 1f,
            1f, -1f, 1f,
            -1f, -1f, 1f,

            -1f, 1f, -1f,
            1f, 1f, -1f,
            1f, 1f, 1f,
            1f, 1f, 1f,
            -1f, 1f, 1f,
            -1f, 1f, -1f,

            -1f, -1f, -1f,
            -1f, -1f, 1f,
            1f, -1f, -1f,
            1f, -1f, -1f,
            -1f, -1f, 1f,
            1f, -1f, 1f
    };

    private FloatBuffer vertexBuffer;

    public CubeMap(Context context, float levelLimits, String assetsPathName) {
        this.context = context;
        this.levelLimits = levelLimits;

        this.makeProgram();
        this.bind();
        this.loadCubeMaptexture(assetsPathName);
        this.makeCube();
    }

    private void bind() {
        this.texCoordHandle = GLES20.glGetAttribLocation(this.mProgram, "a_vp");
        this.mvpMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "u_MVPMatrix");
        this.samplerCubeHandle = GLES20.glGetUniformLocation(this.mProgram, "u_cube_map");
    }

    private void makeProgram() {
        int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.cube_map_vs));
        int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.cube_map_fs));

        this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(this.mProgram);
    }

    private void loadCubeMaptexture(String assetsPathName) {

        GLES20.glGenTextures(1, textureCubeID, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureCubeID[0]);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapLoader.getBitmapFromAsset(this.context, assetsPathName + "posx.jpg");
        ByteBuffer b = ByteBuffer.allocateDirect(bitmap.getHeight() * bitmap.getWidth() * 4);
        bitmap.copyPixelsToBuffer(b);
        b.position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, b);

        bitmap = BitmapLoader.getBitmapFromAsset(this.context, assetsPathName + "negx.jpg");
        b = ByteBuffer.allocateDirect(bitmap.getHeight() * bitmap.getWidth() * 4);
        bitmap.copyPixelsToBuffer(b);
        b.position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, b);

        bitmap = BitmapLoader.getBitmapFromAsset(this.context, assetsPathName + "posy.jpg");
        b = ByteBuffer.allocateDirect(bitmap.getHeight() * bitmap.getWidth() * 4);
        bitmap.copyPixelsToBuffer(b);
        b.position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, b);

        bitmap = BitmapLoader.getBitmapFromAsset(this.context, assetsPathName + "negy.jpg");
        b = ByteBuffer.allocateDirect(bitmap.getHeight() * bitmap.getWidth() * 4);
        bitmap.copyPixelsToBuffer(b);
        b.position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, b);

        bitmap = BitmapLoader.getBitmapFromAsset(this.context, assetsPathName + "posz.jpg");
        b = ByteBuffer.allocateDirect(bitmap.getHeight() * bitmap.getWidth() * 4);
        bitmap.copyPixelsToBuffer(b);
        b.position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, b);

        bitmap = BitmapLoader.getBitmapFromAsset(this.context, assetsPathName + "negz.jpg");
        b = ByteBuffer.allocateDirect(bitmap.getHeight() * bitmap.getWidth() * 4);
        bitmap.copyPixelsToBuffer(b);
        b.position(0);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, b);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    private void makeCube() {
        this.vertexBuffer = ByteBuffer.allocateDirect(this.pointsCubeMap.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        this.vertexBuffer.put(this.pointsCubeMap)
                .position(0);
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix) {
        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, this.levelLimits, this.levelLimits, this.levelLimits);
        float[] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix.clone(), 0, mModelMatrix, 0);
        GLES20.glUseProgram(this.mProgram);

        GLES20.glUniformMatrix4fv(this.mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureCubeID[0]);
        GLES20.glUniform1i(this.samplerCubeHandle, 0);

        this.vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(this.texCoordHandle, 3, GLES20.GL_FLOAT, false, 0, this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(this.texCoordHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.pointsCubeMap.length / 3);

        GLES20.glDisableVertexAttribArray(this.texCoordHandle);
    }
}
