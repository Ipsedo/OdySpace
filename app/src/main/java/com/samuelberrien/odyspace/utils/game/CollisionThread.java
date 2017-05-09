package com.samuelberrien.odyspace.utils.game;

/**
 * Created by samuel on 09/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class CollisionThread extends CancelableThread {

    public CollisionThread(Level level) {
        super("CollisionThread", level);
    }

    @Override
    public void work() {
        super.level.collide();
        try {
            Thread.sleep(CancelableThread.TIME_TO_WAIT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}