package com.samuelberrien.odyspace.game;

/**
 * Created by samuel on 16/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.controls.GamePad;
import com.samuelberrien.odyspace.drawable.text.GameOver;
import com.samuelberrien.odyspace.drawable.text.LevelDone;
import com.samuelberrien.odyspace.objects.baseitem.shooters.Ship;
import com.samuelberrien.odyspace.utils.game.FireType;
import com.samuelberrien.odyspace.utils.game.Level;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

	private Context context;

	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];

	private final float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
	private final float[] mLightPosInEyeSpace = new float[4];
	private final float[] mLightModelMatrix = new float[16];
	private final float[] mLightPosInWorldSpace = new float[4];

	private float[] mCameraPosition = new float[3];
	private float[] mCameraDirection = new float[3];
	private float[] mCameraUpVec = new float[3];
	private float maxRange = 1f;
	private float projectionAngle = 40f;
	private float maxProjDist = 1200f;
	private float ratio = 1f;

	private GamePad gamePad;

	private Level currentLevel;

	private Ship ship;

	private long currTime = System.currentTimeMillis();

	private GameOver gameOver;
	private LevelDone levelDone;

	private boolean isInit = false;

	public MyGLRenderer(Context context, Level currentLevel, GamePad gamePad) {
		this.context = context;
		this.gamePad = gamePad;
		this.currentLevel = currentLevel;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClearColor(0.1f, 0.0f, 0.3f, 1.0f);

		FireType.initAmmos(context);

		if (!isInit) {
			mCameraDirection = new float[]{
					mCameraPosition[0],
					mCameraPosition[1],
					mCameraPosition[2] + 1f};

			gamePad.initGraphics(context);

			ship = Ship.makeShip(context, gamePad);
			ship.update();

			mCameraPosition = new float[]{0f, 0f, -10f};
			mCameraUpVec = new float[]{0f, 1f, 0f};

			currentLevel.init(context, ship, 500f);

			updateCameraPosition(ship.getCamPosition());
			updateCamLookVec(ship.getCamLookAtVec());
			updateCamUpVec(ship.getCamUpVec());

			gameOver = new GameOver(context);
			levelDone = new LevelDone(context);

			isInit = true;
		}
	}

	/**
	 * Update the camera look at vector (normalized)
	 *
	 * @param xyz The x y z vector
	 */
	private void updateCamLookVec(float[] xyz) {
		mCameraDirection[0] = maxRange * xyz[0] + mCameraPosition[0];
		mCameraDirection[1] = maxRange * xyz[1] + mCameraPosition[1];
		mCameraDirection[2] = maxRange * xyz[2] + mCameraPosition[2];
	}

	/**
	 * Update the camera up vector
	 *
	 * @param xyz The x y z vector
	 */
	private void updateCamUpVec(float[] xyz) {
		mCameraUpVec[0] = xyz[0];
		mCameraUpVec[1] = xyz[1];
		mCameraUpVec[2] = xyz[2];
	}

	/**
	 * Update the camera position
	 *
	 * @param mCameraPosition A 3D vector contening x, y and z new camera position
	 */
	private void updateCameraPosition(float[] mCameraPosition) {
		this.mCameraPosition = mCameraPosition;
	}


	private void updateLight(float[] pos) {
		Matrix.setIdentityM(mLightModelMatrix, 0);
		Matrix.translateM(mLightModelMatrix, 0, pos[0], pos[1], pos[2]);
		Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
		Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		updateCameraPosition(ship.getCamPosition());
		updateCamLookVec(ship.getCamLookAtVec());
		updateCamUpVec(ship.getCamUpVec());

		Matrix.perspectiveM(mProjectionMatrix, 0, projectionAngle, ratio, 1, maxProjDist);
		Matrix.setLookAtM(mViewMatrix, 0,
				mCameraPosition[0], mCameraPosition[1], mCameraPosition[2],
				mCameraDirection[0], mCameraDirection[1], mCameraDirection[2],
				mCameraUpVec[0], mCameraUpVec[1], mCameraUpVec[2]);

		updateLight(currentLevel.getLightPos());

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		currentLevel.draw(mProjectionMatrix, mViewMatrix, mLightPosInEyeSpace, mCameraPosition);

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		gamePad.draw(ratio);
		ship.drawLife(ratio);
		currentLevel.drawLevelInfo(ratio);

		if (currentLevel.isDead()) {
			gameOver.draw(ratio);
		} else if (currentLevel.isWinner()) {
			levelDone.draw(ratio);
		}

		System.out.println("FPS : " + 1000L / (System.currentTimeMillis() - currTime));
		currTime = System.currentTimeMillis();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		ratio = (float) width / height;

		//Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 50f);
		Matrix.perspectiveM(mProjectionMatrix, 0, projectionAngle, ratio, 1, maxProjDist);
	}
}
