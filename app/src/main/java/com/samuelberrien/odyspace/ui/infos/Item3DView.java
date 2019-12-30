package com.samuelberrien.odyspace.ui.infos;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.ObjModelMtlVBO;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 12/10/17.
 */

public abstract class Item3DView extends GLSurfaceView implements GLSurfaceView.Renderer {

	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	private float[] mItemModelMatrix = new float[16];
	private float angle;

	private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	private final float[] mLightPosInEyeSpace = new float[4];
	private final float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInWorldSpace = new float[4];

	protected String objFileName;
	protected String mtlFileName;
	//private Context glContext;

	private ObjModelMtlVBO objModelMtlVBO;

	protected boolean willCreateObj;

	public Item3DView(Context context, String name) {
		super(context);
		//this.glContext = glContext;
		angle = (float) (Math.random() * 360d);

		changeObj(name);

		setEGLContextClientVersion(2);
		setRenderer(this);
	}

	public abstract void changeObj(String name);

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClearColor(0.6f, 0.6f, 0.6f, 1.0f);

		if (willCreateObj) {
			objModelMtlVBO = new ObjModelMtlVBO(getContext(), objFileName, mtlFileName, 1, 0, false);
			willCreateObj = false;
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;

		//Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 50f);
		Matrix.perspectiveM(mProjectionMatrix, 0, 50f, ratio, 1, 50f);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		if (willCreateObj) {
			objModelMtlVBO = new ObjModelMtlVBO(getContext(), objFileName, mtlFileName, 1, 0, false);
			willCreateObj = false;
		}

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -4f, 0f, 0f, 0f, 0f, 1f, 0f);

		Matrix.setRotateM(mItemModelMatrix, 0, angle++, 0f, 1f, 0f);

		float[] mvMatrix = new float[16];
		Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mItemModelMatrix, 0);
		float[] mvpMatrix = new float[16];
		Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mvMatrix, 0);

		updateLight(new float[]{0f, 0f, -4f});

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		objModelMtlVBO.draw(mvpMatrix, mvMatrix, mLightPosInEyeSpace, new float[]{0f, 0f, -4f});
	}

	private void updateLight(float[] pos) {
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, pos[0], pos[1], pos[2]);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
	}
}
