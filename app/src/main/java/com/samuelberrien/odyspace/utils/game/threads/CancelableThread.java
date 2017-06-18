package com.samuelberrien.odyspace.utils.game.threads;

import com.samuelberrien.odyspace.utils.game.Level;

/**
 * Created by samuel on 09/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public abstract class CancelableThread extends Thread {

    public static long TIME_TO_WAIT = 10L;

    private boolean isCanceled;
    protected Level level;

    public CancelableThread(String threadName, Level level) {
        super(threadName);
        this.isCanceled = false;
        this.level = level;
    }

    public void setCanceled(boolean canceled) {
        this.isCanceled = canceled;
    }

    public abstract void work();

    public void run() {
        while (!this.isCanceled && !this.level.isInit()) {
            try {
                Thread.sleep(CancelableThread.TIME_TO_WAIT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (!this.isCanceled) {
            this.work();
        }
    }
}
