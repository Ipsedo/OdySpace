package com.samuelberrien.odyspace.utils.game.threads;

import android.app.Activity;
import android.content.Intent;

import com.samuelberrien.odyspace.game.LevelActivity;
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
        this.resultSetted = false;
    }

    @Override
    public void afterInit() {
        this.levelActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                levelActivity.loadingLevelFinished();
            }
        });
    }

    @Override
    public void work() {
        if (!this.resultSetted && super.level.isDead()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(LevelActivity.LEVEL_RESULT, Integer.toString(0));
            resultIntent.putExtra(LevelActivity.LEVEL_SCORE, Integer.toString(super.level.getScore()));
            this.levelActivity.setResult(Activity.RESULT_OK, resultIntent);
            this.finishGame();
        } else if (!this.resultSetted && super.level.isWinner()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(LevelActivity.LEVEL_RESULT, Integer.toString(1));
            resultIntent.putExtra(LevelActivity.LEVEL_SCORE, Integer.toString(super.level.getScore()));
            this.levelActivity.setResult(Activity.RESULT_OK, resultIntent);
            this.finishGame();
        }
        /*try {
            Thread.sleep(CancelableThread.TIME_TO_WAIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    private void finishGame() {
        this.resultSetted = true;
        Thread tmp = new Thread("StopGameThread") {
            public void run() {
                EndGameThread.this.levelActivity.finish();
            }
        };
        tmp.setPriority(Thread.MAX_PRIORITY);
        tmp.start();
    }
}
