package com.samuelberrien.odyspace.utils.game.thread;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AlertDialog;

import com.samuelberrien.odyspace.R;
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
    private long fstTime;
    private boolean needToStartCounter;

    public EndGameThread(Level level, LevelActivity levelActivity) {
        super("EndGameThread", level);
        this.levelActivity = levelActivity;
        this.fstTime = System.currentTimeMillis();
        this.needToStartCounter = true;
    }

    @Override
    public void work() {
        if (super.level.isDead()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(LevelActivity.LEVEL_RESULT, Integer.toString(0));
            resultIntent.putExtra(LevelActivity.LEVEL_SCORE, Integer.toString(super.level.getScore()));
            this.levelActivity.setResult(Activity.RESULT_OK, resultIntent);
            if(this.needToStartCounter) {
                this.needToStartCounter = false;
                this.fstTime = System.currentTimeMillis();
            }
        }
        if (super.level.isWinner()) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(LevelActivity.LEVEL_RESULT, Integer.toString(1));
            resultIntent.putExtra(LevelActivity.LEVEL_SCORE, Integer.toString(super.level.getScore()));
            this.levelActivity.setResult(Activity.RESULT_OK, resultIntent);
            if(this.needToStartCounter) {
                this.needToStartCounter = false;
                this.fstTime = System.currentTimeMillis();
            }
        }
        if((super.level.isDead() || super.level.isWinner()) && System.currentTimeMillis() - this.fstTime > 1000L ) {
            this.levelActivity.finish();
        }
        try {
            Thread.sleep(CancelableThread.TIME_TO_WAIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
