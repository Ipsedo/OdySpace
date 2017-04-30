package com.samuelberrien.odyspace.utils.game;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.controls.Controls;
import com.samuelberrien.odyspace.drawable.HeightMap;
import com.samuelberrien.odyspace.drawable.controls.Joystick;
import com.samuelberrien.odyspace.objects.Ship;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface Level {

    void init(Context context, Ship ship, float levelLimitSize);

    void draw(float[] mProjectionMatrix, float[] mViewMatrix, float[] mLightPosInEyeSpace, float[] mCameraPosition);

    void update(Joystick joystick, Controls controls);

    void removeObjects();

    boolean isDead();

    boolean isWinner();
}
