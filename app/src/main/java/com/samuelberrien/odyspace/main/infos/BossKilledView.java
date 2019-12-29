package com.samuelberrien.odyspace.main.infos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.ObjModelMtlVBO;
import com.samuelberrien.odyspace.levels.TestBossThread;
import com.samuelberrien.odyspace.core.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by samuel on 13/10/17.
 */

public class BossKilledView
		extends GLSurfaceView
		implements GLSurfaceView.Renderer, SharedPreferences.OnSharedPreferenceChangeListener {

	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];
	private ArrayList<float[]> mItemModelMatrix = new ArrayList<>();
	private ArrayList<Float> mAngles = new ArrayList<>();

	private int nbBeatedBoss;

	private ArrayList<ObjModelMtlVBO> boss = new ArrayList<>();

	private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	private final float[] mLightPosInEyeSpace = new float[4];
	private final float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInWorldSpace = new float[4];

	private SharedPreferences levelPreferences;

	private boolean willCreateBoss;

	private Random random;

	private ArrayList<String> objFileNames;

	public BossKilledView(Context context) {
		super(context);
		init();
	}

	public BossKilledView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);

		setEGLContextClientVersion(2);
		setRenderer(this);

		objFileNames = new ArrayList<>();
		objFileNames.add("skull");
		random = new Random(System.currentTimeMillis());
		willCreateBoss = false;
		levelPreferences = getContext().getSharedPreferences(
				getContext().getString(R.string.level_info_preferences),
				Context.MODE_PRIVATE);
		levelPreferences.registerOnSharedPreferenceChangeListener(this);
		updateBeatedBoss();
	}

	private void updateBeatedBoss() {
		int currLevel = levelPreferences.getInt(
				getContext().getString(R.string.saved_max_level),
				getResources().getInteger(R.integer.saved_max_level_default));
		mItemModelMatrix.clear();
		mAngles.clear();
		boss.clear();
		nbBeatedBoss = 0;
		for (int i = 0; i < currLevel; i++) {
			if (Level.LEVELS[i].equals(TestBossThread.NAME)) {
				nbBeatedBoss++;
			}
		}
		willCreateBoss = true;
	}

	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClearColor(0f, 0f, 0f, 0f);
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		float ratio = (float) width / height;

		//Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 50f);
		Matrix.perspectiveM(mProjectionMatrix, 0, 50f, ratio, 1, 50f);
	}

	private void updateLight(float[] pos) {
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, pos[0], pos[1], pos[2]);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {
		if (willCreateBoss) {
			for (int i = 0; i < nbBeatedBoss; i++) {
				mItemModelMatrix.add(new float[16]);
				mAngles.add(random.nextFloat() * 360f);
				String fileName = objFileNames.get(i);
				boss.add(new ObjModelMtlVBO(getContext(),
						"obj/" + fileName + ".obj", "obj/" + fileName + ".mtl",
						1f, 0f, false));
			}
			willCreateBoss = false;
		}

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -8f, 0f, 0f, 0f, 0f, 1f, 0f);

		updateLight(new float[]{0f, 0f, -8f});

		float[] mvMatrix = new float[16];
		float[] mvpMatrix = new float[16];

		for (int i = 0; i < mItemModelMatrix.size(); i++) {
			float[] tmp = new float[16];
			Matrix.setIdentityM(tmp, 0);
			Matrix.translateM(tmp, 0, 6f - i * 2f, 0f, 0f);
			float[] tmp2 = new float[16];
			Matrix.setRotateM(tmp2, 0, mAngles.get(i), 0f, 1f, 0f);
			Matrix.multiplyMM(tmp, 0, tmp.clone(), 0, tmp2, 0);
			mItemModelMatrix.set(i, tmp);
			mAngles.set(i, mAngles.get(i) + 1);

			Matrix.multiplyMM(mvMatrix, 0, mViewMatrix, 0, mItemModelMatrix.get(i), 0);
			Matrix.multiplyMM(mvpMatrix, 0, mProjectionMatrix, 0, mvMatrix, 0);

			boss.get(i).draw(mvpMatrix, mvMatrix, mLightPosInEyeSpace, new float[]{0f, 0f, -8f});
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		if (s.equals(getContext().getString(R.string.saved_max_level))) {
			updateBeatedBoss();
		}
	}
}
