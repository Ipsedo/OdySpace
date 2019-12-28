package com.samuelberrien.odyspace.core;

import com.samuelberrien.odyspace.core.collision.Box;

/**
 * Created by samuel on 14/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface Item {

	/**
	 * Make the collision test with the given triangle array and its model matrix in the box container
	 *
	 * @param triangleArray
	 * @param modelMatrix
	 * @param container
	 * @return
	 */
	boolean collideTest(float[] triangleArray, float[] modelMatrix, Box container);

	boolean isCollided(Item other);

	boolean isInside(Box box);

	boolean isAlive();

	int getDamage();

	void decrementLife(int minus);

	float[] clonePosition();
}
