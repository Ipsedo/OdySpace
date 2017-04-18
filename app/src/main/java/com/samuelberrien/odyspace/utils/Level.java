package com.samuelberrien.odyspace.utils;

import com.samuelberrien.odyspace.drawable.Joystick;
import com.samuelberrien.odyspace.objects.Ship;

/**
 * Created by samuel on 18/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public interface Level {

    public void init(Ship ship);

    public void draw();

    public void update(Joystick joystick);

    public boolean isDead();

    public boolean isWinner();
}
