package com.samuelberrien.odyspace.drawable.maps;

import com.samuelberrien.odyspace.drawable.GLDrawable;

/**
 * Created by samuel on 11/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface Map extends GLDrawable {

	float[] getRestreintArea(float[] position);

	float[] passToModelMatrix(float[] triangles);

	void update();
}
