package com.samuelberrien.odyspace.game;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.samuelberrien.odyspace.controls.GamePad;
import com.samuelberrien.odyspace.levels.TestBossThread;
import com.samuelberrien.odyspace.levels.TestProtectionLevel;
import com.samuelberrien.odyspace.levels.TestSpaceTrip;
import com.samuelberrien.odyspace.levels.TestThread;
import com.samuelberrien.odyspace.levels.TestTunnelLevel;
import com.samuelberrien.odyspace.levels.TestTurrets;
import com.samuelberrien.odyspace.core.Level;
import com.samuelberrien.odyspace.core.threads.CollisionThread;
import com.samuelberrien.odyspace.core.threads.EndGameThread;
import com.samuelberrien.odyspace.core.threads.RemoveThread;
import com.samuelberrien.odyspace.core.threads.UpdateThread;

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

	/**
	 * @param context
	 * @param levelActivity
	 * @param levelID
	 */
	public MyGLSurfaceView(Context context, LevelActivity levelActivity, int levelID) {
		super(context);
		this.context = context;
		this.levelActivity = levelActivity;
		setEGLContextClientVersion(2);
		gamePad = new GamePad();

		currentLevel = getCurrentLevel(levelID);

		renderer = new MyGLRenderer(this.context, currentLevel, gamePad);
		setRenderer(renderer);

		setPreserveEGLContextOnPause(true);
	}

	public boolean isInit() {
		return currentLevel.isInit();
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
		} else if (currLevelId == 4) {
			return new TestTunnelLevel();
		} else {
			return new TestSpaceTrip();
		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		initThreads();
	}

	@Override
	public void onDetachedFromWindow() {
		killThread();
		super.onDetachedFromWindow();
	}

	private void initThreads() {
		if (collisionThread == null || collisionThread.isCanceled()) {
			collisionThread = new CollisionThread(currentLevel);
			collisionThread.start();
		}
		if (updateThread == null || updateThread.isCanceled()) {
			updateThread = new UpdateThread(currentLevel);
			updateThread.start();
		}
		if (removeThread == null || removeThread.isCanceled()) {
			removeThread = new RemoveThread(currentLevel);
			removeThread.start();
		}
		if (endGameThread == null || endGameThread.isCanceled()) {
			endGameThread = new EndGameThread(currentLevel, levelActivity);
			endGameThread.start();
		}
	}

	private void killThread() {
		collisionThread.cancel();
		updateThread.cancel();
		removeThread.cancel();
		endGameThread.cancel();
		try {
			collisionThread.join();
			updateThread.join();
			removeThread.join();
			endGameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void resumeGame() {
		initThreads();
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}

	public void pauseGame() {
		killThread();
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		gamePad.update(e, getWidth(), getHeight());
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		initThreads();
	}

	@Override
	public void onPause() {
		killThread();
		super.onPause();
	}

	public String getScore() {
		return Integer.toString(currentLevel.getScore());
	}
}
