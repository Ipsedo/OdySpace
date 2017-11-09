package com.samuelberrien.odyspace.utils.game.threads;

import android.app.Activity;
import android.content.Intent;

import com.samuelberrien.odyspace.game.LevelActivity;
import com.samuelberrien.odyspace.main.MainActivity;
import com.samuelberrien.odyspace.utils.game.Level;

/**
 * Created by samuel on 09/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class EndGameThread extends CancelableThread {

	private LevelActivity levelActivity;
	private boolean resultSetted;

	public EndGameThread(Level level, LevelActivity levelActivity) {
		super("EndGameThread", level);
		this.levelActivity = levelActivity;
		resultSetted = false;
		setPriority(Thread.MIN_PRIORITY);
		timeToWait = 300L;
	}

	@Override
	public void afterInit() {
		levelActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				levelActivity.loadingLevelFinished();
			}
		});
	}

	@Override
	public void work() {
		if (!resultSetted && level.isDead()) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra(LevelActivity.LEVEL_RESULT, LevelActivity.FAIIL);
			resultIntent.putExtra(LevelActivity.LEVEL_SCORE, level.getScore());
			levelActivity.setResult(Activity.RESULT_OK, resultIntent);
			finishGame();
		} else if (!resultSetted && level.isWinner()) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra(LevelActivity.LEVEL_RESULT, LevelActivity.WIN);
			resultIntent.putExtra(LevelActivity.LEVEL_SCORE, level.getScore());
			resultIntent.putExtra(MainActivity.LEVEL_ID, findLevelIndex());
			levelActivity.setResult(Activity.RESULT_OK, resultIntent);
			finishGame();
		}
	}

	private void finishGame() {
		resultSetted = true;
		Thread tmp = new Thread("StopGameThread") {
			public void run() {
				levelActivity.finish();
			}
		};
		tmp.setPriority(Thread.MAX_PRIORITY);
		tmp.start();
	}

	private int findLevelIndex() {
		for(int i = 0; i < Level.LEVELS.length; i++) {
			if(Level.LEVELS[i].equals(level.toString()))
				return i;
		}
		return -1;
	}
}
