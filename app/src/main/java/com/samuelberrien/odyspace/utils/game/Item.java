package com.samuelberrien.odyspace.utils.game;

/**
 * Created by samuel on 14/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface Item {

    boolean collideTest(float[] triangleArray, float[] modelMatrix);

    boolean isCollided(Item other);

    boolean isInside(LevelLimits levelLimits);

    int getDamage();

    void decrementLife(int minus);
}
