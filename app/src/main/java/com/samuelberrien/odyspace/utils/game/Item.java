package com.samuelberrien.odyspace.utils.game;

import com.samuelberrien.odyspace.utils.collision.Box;

/**
 * Created by samuel on 14/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface Item {

	boolean collideTest(float[] triangleArray, float[] modelMatrix, Box container);

	boolean isCollided(Item other);

	boolean isInside(Box box);

	int getDamage();

	void decrementLife(int minus);

	float[] getPosition();
}
