package com.samuelberrien.odyspace.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.controls.GamePad;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.levels.TestBossThread;
import com.samuelberrien.odyspace.levels.TestProtectionLevel;
import com.samuelberrien.odyspace.levels.TestThread;
import com.samuelberrien.odyspace.levels.TestTunnelLevel;
import com.samuelberrien.odyspace.levels.TestTurrets;
import com.samuelberrien.odyspace.utils.game.FireType;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.game.threads.CollisionThread;
import com.samuelberrien.odyspace.utils.game.threads.EndGameThread;
import com.samuelberrien.odyspace.utils.game.threads.RemoveThread;
import com.samuelberrien.odyspace.utils.game.threads.UpdateThread;

/**
 * Created by samuel on 16/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class MyGLSurfaceView extends GLSurfaceView {

	private Context context;
	private LevelActivity levelActivity;

	private MyGLRenderer renderer;

	private Level currentLevel;
	private GamePad gamePad;

	private CollisionThread collisionThread;
	private UpdateThread updateThread;
	private RemoveThread removeThread;
	private EndGameThread endGameThread;
	private boolean threadRunning;

	/**
	 * @param context
	 * @param levelActivity
	 * @param levelID
	 */
	public MyGLSurfaceView(Context context, LevelActivity levelActivity, int levelID) {
		super(context);
		this.context = context;
		this.levelActivity = levelActivity;
		this.setEGLContextClientVersion(2);
		this.gamePad = new GamePad();

		this.currentLevel = this.getCurrentLevel(levelID);

		this.threadRunning = false;

		this.renderer = new MyGLRenderer(this.context, this.currentLevel, this.gamePad);
		this.setRenderer(this.renderer);

		this.setPreserveEGLContextOnPause(true);
	}

	public boolean isInit() {
		return this.currentLevel.isInit();
	}

	public void inversePitchJoystick(boolean isInversed) {
		this.gamePad.inversePitch(isInversed);
	}

	public void inverseYawRoll(boolean isInversed) {
		this.gamePad.inverseRollAndYaw(isInversed);
	}

	private Level getCurrentLevel(int currLevelId) {
		if (currLevelId == 0) {
			return new TestThread();
		} else if (currLevelId == 1) {
			return new TestProtectionLevel();
		} else if (currLevelId == 2) {
			return new TestTurrets();
		} else if (currLevelId == 3) {
			return new TestBossThread();
		} else {
			return new TestTunnelLevel();
		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		this.initThreads();
	}

	@Override
	public void onDetachedFromWindow() {
		this.killThread();
		super.onDetachedFromWindow();
	}

	private void initThreads() {
		if (!this.threadRunning) {
			this.collisionThread = new CollisionThread(this.currentLevel);
			this.updateThread = new UpdateThread(this.currentLevel);
			this.removeThread = new RemoveThread(this.currentLevel);
			this.endGameThread = new EndGameThread(this.currentLevel, this.levelActivity);
			this.collisionThread.start();
			this.updateThread.start();
			this.removeThread.start();
			this.endGameThread.start();
			this.threadRunning = true;
		}
	}

	private void killThread() {
		if (this.threadRunning) {
			this.collisionThread.setCanceled(true);
			this.updateThread.setCanceled(true);
			this.removeThread.setCanceled(true);
			this.endGameThread.setCanceled(true);
			try {
				this.collisionThread.join();
				this.updateThread.join();
				this.removeThread.join();
				this.endGameThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.threadRunning = false;
		}
	}

	public void resumeGame() {
		this.initThreads();
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	public void pauseGame() {
		this.killThread();
		this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		this.gamePad.update(e, this.getWidth(), this.getHeight());
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.initThreads();
	}

	@Override
	public void onPause() {
		this.killThread();
		super.onPause();
	}

	public void setShipFireType(FireType fireType) {
		this.renderer.setShipFireType(fireType);
	}

	public String getScore() {
		return Integer.toString(this.currentLevel.getScore());
	}
}
