package com.samuelberrien.odyspace.drawable.maps;

import com.samuelberrien.odyspace.utils.game.Item;

/**
 * Created by samuel on 11/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface Map extends Item {

    float[] getModelMatrix();

    float[] getRestreintArea(float[] position);

    float[] passToModelMatrix(float[] triangles);

    void update();

    void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace);
}
