package com.samuelberrien.odyspace.game;

/**
 * Created by samuel on 16/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.drawable.text.GameOver;
import com.samuelberrien.odyspace.drawable.text.LevelDone;
import com.samuelberrien.odyspace.objects.baseitem.Ship;
import com.samuelberrien.odyspace.utils.game.Level;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

	private Context context;

	private MyGLSurfaceView myGLSurfaceView;

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

	private Joystick joystick;
	private Controls controls;

	private Level currentLevel;

	private Ship ship;

	private long currTime = System.currentTimeMillis();

	private GameOver gameOver;
	private LevelDone levelDone;

	private SharedPreferences savedShop;
	private SharedPreferences savedShip;

	/**
	 * @param context
	 */
	public MyGLRenderer(Context context, MyGLSurfaceView myGLSurfaceView, Level currentLevel, Joystick joystick, Controls controls) {
		this.context = context;
		this.myGLSurfaceView = myGLSurfaceView;
		this.joystick = joystick;
		this.controls = controls;
		this.currentLevel = currentLevel;
		this.savedShop = this.context.getSharedPreferences(this.context.getString(R.string.saved_shop), Context.MODE_PRIVATE);
		this.savedShip = this.context.getSharedPreferences(this.context.getString(R.string.saved_ship_info), Context.MODE_PRIVATE);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		this.mCameraDirection = new float[]{this.mCameraPosition[0], this.mCameraPosition[1], this.mCameraPosition[2] + 1f};

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
		GLES20.glDepthMask(true);
		GLES20.glClearColor(0.1f, 0.0f, 0.3f, 1.0f);

		this.joystick.initGraphics(this.context);
		this.controls.initGraphics(this.context);

		this.ship = Ship.makeShip(this.context);
		this.ship.setGameControls(this.joystick, this.controls);
		this.ship.move();

		this.mCameraPosition = new float[]{0f, 0f, -10f};
		this.mCameraUpVec = new float[]{0f, 1f, 0f};

		this.currentLevel.init(this.context, this.ship, 500f);

		this.updateCameraPosition(this.ship.getCamPosition());
		this.updateCamLookVec(this.ship.getCamLookAtVec());
		this.updateCamUpVec(this.ship.getCamUpVec());

		this.gameOver = new GameOver(this.context);
		this.levelDone = new LevelDone(this.context);
	}

	/**
	 * Update the camera look at vector (normalized)
	 *
	 * @param xyz The x y z vector
	 */
	private void updateCamLookVec(float[] xyz) {
		this.mCameraDirection[0] = this.maxRange * xyz[0] + this.mCameraPosition[0];
		this.mCameraDirection[1] = this.maxRange * xyz[1] + this.mCameraPosition[1];
		this.mCameraDirection[2] = this.maxRange * xyz[2] + this.mCameraPosition[2];
	}

	/**
	 * Update the camera up vector
	 *
	 * @param xyz The x y z vector
	 */
	private void updateCamUpVec(float[] xyz) {
		this.mCameraUpVec[0] = xyz[0];
		this.mCameraUpVec[1] = xyz[1];
		this.mCameraUpVec[2] = xyz[2];
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
		Matrix.setIdentityM(this.mLightModelMatrix, 0);
		Matrix.translateM(this.mLightModelMatrix, 0, pos[0], pos[1], pos[2]);
		Matrix.multiplyMV(this.mLightPosInWorldSpace, 0, this.mLightModelMatrix, 0, this.mLightPosInModelSpace, 0);
		Matrix.multiplyMV(this.mLightPosInEyeSpace, 0, this.mViewMatrix, 0, this.mLightPosInWorldSpace, 0);
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		this.updateCameraPosition(this.ship.getCamPosition());
		this.updateCamLookVec(this.ship.getCamLookAtVec());
		this.updateCamUpVec(this.ship.getCamUpVec());

		Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, this.ratio, 1, this.maxProjDist);
		Matrix.setLookAtM(this.mViewMatrix, 0, this.mCameraPosition[0], this.mCameraPosition[1], this.mCameraPosition[2], this.mCameraDirection[0], this.mCameraDirection[1], this.mCameraDirection[2], this.mCameraUpVec[0], this.mCameraUpVec[1], this.mCameraUpVec[2]);

		this.updateLight(this.currentLevel.getLightPos());

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		this.currentLevel.draw(this.mProjectionMatrix, this.mViewMatrix, this.mLightPosInEyeSpace, this.mCameraPosition);

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		this.joystick.draw(this.ratio);
		this.controls.draw(this.ratio);
		this.ship.drawLife(this.ratio);
		this.currentLevel.drawLevelInfo(this.ratio);

		if (this.currentLevel.isDead()) {
			this.gameOver.draw(this.ratio);
		} else if (this.currentLevel.isWinner()) {
			this.levelDone.draw(this.ratio);
		}

		System.out.println("FPS : " + 1000L / (System.currentTimeMillis() - this.currTime));
		this.currTime = System.currentTimeMillis();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		this.ratio = (float) width / height;

		//Matrix.frustumM(mProjectionMatrix, 0, -this.ratio, this.ratio, -1, 1, 3, 50f);
		Matrix.perspectiveM(this.mProjectionMatrix, 0, this.projectionAngle, this.ratio, 1, this.maxProjDist);
	}
}
