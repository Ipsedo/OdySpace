package com.samuelberrien.odyspace.drawable.maps;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.graphics.ShaderLoader;
import com.samuelberrien.odyspace.utils.maths.SimplexNoise;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by samuel on 11/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class NoiseMap {

    private Context context;

    private final int SIZE = 30;

    private float lightCoeff;
    private float distanceCoeff;

    private float scale;
    private float limitHeight;

    private float[] points;
    private float[] normals;
    private FloatBuffer mPositions;
    private FloatBuffer mNormals;

    private float[] mModelMatrix;

    private final int mProgram;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mLightPosHandle;
    private int mMVMatrixHandle;
    private int mDistanceCoefHandle;
    private int mLightCoefHandle;

    static final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private float[] color = new float[]{1f, 0f, 0f, 1f};

    public NoiseMap(Context context, float lightCoeff, float distanceCoeff, float scale, float limitHeight) {
        this.context = context;
        this.lightCoeff = lightCoeff;
        this.distanceCoeff = distanceCoeff;
        this.scale = scale;
        this.limitHeight = limitHeight;

        this.mModelMatrix = new float[16];

        int vertexShader = ShaderLoader.loadShader(GLES20.GL_VERTEX_SHADER, ShaderLoader.openShader(context, R.raw.noise_map_vs));
        int fragmentShader = ShaderLoader.loadShader(GLES20.GL_FRAGMENT_SHADER, ShaderLoader.openShader(context, R.raw.noise_map_fs));

        this.mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(this.mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(this.mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(this.mProgram);

        this.bind();

        this.initPlan();
    }

    private void initPlan() {
        ArrayList<Float> triangles = new ArrayList<>();
        ArrayList<Float> normales = new ArrayList<>();

        for (int i = 0; i < SIZE; i++) {
            float[] tmpPoints = new float[(SIZE + 1) * 2 * 3];
            for (int j = 0; j < SIZE + 1; j++) {
                tmpPoints[j * 2 * 3] = (float) j / (float) SIZE;
                tmpPoints[j * 2 * 3 + 1] = (float) SimplexNoise.noise((double) i / (double) (SIZE / 8), (double) j / (double) (SIZE / 8)) / (this.scale * 0.1f);
                tmpPoints[j * 2 * 3 + 2] = (float) i / (float) SIZE;

                tmpPoints[(j * 2 + 1) * 3] = (float) j / (float) SIZE;
                tmpPoints[(j * 2 + 1) * 3 + 1] = (float) SimplexNoise.noise((double) (i + 1) / (double) (SIZE / 8), (double) j / (double) (SIZE / 8)) / (this.scale * 0.1f);
                tmpPoints[(j * 2 + 1) * 3 + 2] = ((float) i + 1) / (float) SIZE;
            }
            for (int j = 0; j < tmpPoints.length / 3 - 2; j += 2) {

                //Triangle 1
                triangles.add(tmpPoints[j * 3 + 0]);
                triangles.add(tmpPoints[j * 3 + 1]);
                triangles.add(tmpPoints[j * 3 + 2]);
                triangles.add(tmpPoints[j * 3 + 3]);
                triangles.add(tmpPoints[j * 3 + 4]);
                triangles.add(tmpPoints[j * 3 + 5]);
                triangles.add(tmpPoints[j * 3 + 6]);
                triangles.add(tmpPoints[j * 3 + 7]);
                triangles.add(tmpPoints[j * 3 + 8]);

                //Normal 1
                float[] v1 = new float[]{tmpPoints[j * 3 + 6] - tmpPoints[j * 3 + 0], tmpPoints[j * 3 + 7] - tmpPoints[j * 3 + 1], tmpPoints[j * 3 + 8] - tmpPoints[j * 3 + 2]};
                float[] v2 = new float[]{tmpPoints[j * 3 + 3] - tmpPoints[j * 3 + 0], tmpPoints[j * 3 + 4] - tmpPoints[j * 3 + 1], tmpPoints[j * 3 + 5] - tmpPoints[j * 3 + 2]};
                float[] normal = Vector.normalize3f(Vector.cross3f(v2, v1));
                normales.add(normal[0]);
                normales.add(normal[1]);
                normales.add(normal[2]);
                normales.add(normal[0]);
                normales.add(normal[1]);
                normales.add(normal[2]);
                normales.add(normal[0]);
                normales.add(normal[1]);
                normales.add(normal[2]);

                //Triangle 2

                triangles.add(tmpPoints[(j + 1) * 3 + 3]);
                triangles.add(tmpPoints[(j + 1) * 3 + 4]);
                triangles.add(tmpPoints[(j + 1) * 3 + 5]);
                triangles.add(tmpPoints[(j + 1) * 3 + 0]);
                triangles.add(tmpPoints[(j + 1) * 3 + 1]);
                triangles.add(tmpPoints[(j + 1) * 3 + 2]);
                triangles.add(tmpPoints[(j + 1) * 3 + 6]);
                triangles.add(tmpPoints[(j + 1) * 3 + 7]);
                triangles.add(tmpPoints[(j + 1) * 3 + 8]);

                //Normal 2
                v1 = new float[]{tmpPoints[j * 3 + 9] - tmpPoints[j * 3 + 3], tmpPoints[j * 3 + 10] - tmpPoints[j * 3 + 4], tmpPoints[j * 3 + 11] - tmpPoints[j * 3 + 5]};
                v2 = new float[]{tmpPoints[j * 3 + 6] - tmpPoints[j * 3 + 3], tmpPoints[j * 3 + 7] - tmpPoints[j * 3 + 4], tmpPoints[j * 3 + 8] - tmpPoints[j * 3 + 5]};
                normal = Vector.normalize3f(Vector.cross3f(v1, v2));
                normales.add(normal[0]);
                normales.add(normal[1]);
                normales.add(normal[2]);
                normales.add(normal[0]);
                normales.add(normal[1]);
                normales.add(normal[2]);
                normales.add(normal[0]);
                normales.add(normal[1]);
                normales.add(normal[2]);
            }
        }
        System.out.println("ENDD : " + triangles.size());

        this.points = new float[triangles.size()];
        this.normals = new float[normales.size()];
        for (int i = 0; i < this.points.length; i++) {
            this.points[i] = triangles.get(i);
            this.normals[i] = normales.get(i);
        }

        this.mPositions = ByteBuffer.allocateDirect(this.points.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mPositions.put(this.points).position(0);

        this.mNormals = ByteBuffer.allocateDirect(this.normals.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mNormals.put(this.normals).position(0);
    }

    private void bind() {
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Amb_Color");
        mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
        mDistanceCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_distance_coef");
        mLightCoefHandle = GLES20.glGetUniformLocation(mProgram, "u_light_coef");
        mNormalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
    }

    public float[] getRestreintArea(float[] position) {
        float xNorm = position[0] / this.scale + 0.5f;
        float zNorm = position[2] / this.scale + 0.5f;

        float pas = 1f / (float) SIZE;

        int i = (int) (xNorm / pas);
        int j = (int) (zNorm / pas);

        // Jusqu'à la OK

        int startI = Math.max(0, (i) * 2 * 3);
        int endI = Math.min(SIZE * 2 * 3, (i) * 2 * 3);

        int startJ = Math.max(0, (j) * 2);
        int endJ = Math.min(SIZE * 2 * 3, (j) * 2 * 3);

        ArrayList<Float> tmp = new ArrayList<>();
        for (int a = startJ; a <= endJ; a++) {
            for (int b = startI; b <= endI; b++) {
                tmp.add(this.points[(a * SIZE + b) * 3 + 0]);
                tmp.add(this.points[(a * SIZE + b) * 3 + 1]);
                tmp.add(this.points[(a * SIZE + b) * 3 + 2]);
            }
        }

        float[] res = new float[tmp.size()];
        for (int k = 0; k < res.length; k++) {
            res[k] = tmp.get(k);
        }

        System.out.println("ENDDD : " + res.length + " / " + this.points.length);

        return res;
    }

    public float[] getModelMatrix() {
        return this.mModelMatrix;
    }

    public void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace) {

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, -0.5f * this.scale, this.limitHeight, -0.5f * this.scale);
        Matrix.scaleM(mModelMatrix, 0, this.scale, this.scale, this.scale);

        this.mModelMatrix = mModelMatrix;

        float[] mvpMatrix = new float[16];
        float[] mvMatrix = new float[16];
        Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mvMatrix, 0);


        GLES20.glUseProgram(mProgram);

        this.mPositions.position(0);
        this.mNormals.position(0);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(this.mPositionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(this.mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, this.mPositions);

        GLES20.glUniform4fv(this.mColorHandle, 1, this.color, 0);

        GLES20.glEnableVertexAttribArray(mNormalHandle);
        GLES20.glVertexAttribPointer(this.mNormalHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, this.mNormals);

        // get handle to shape's transformation matrix
        GLES20.glUniformMatrix4fv(this.mMVMatrixHandle, 1, false, mvMatrix, 0);

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUniform3fv(mLightPosHandle, 1, mLightPosInEyeSpace, 0);

        GLES20.glUniform1f(mDistanceCoefHandle, this.distanceCoeff);

        GLES20.glUniform1f(mLightCoefHandle, this.lightCoeff);

        // Draw the polygon
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.points.length / 3);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
