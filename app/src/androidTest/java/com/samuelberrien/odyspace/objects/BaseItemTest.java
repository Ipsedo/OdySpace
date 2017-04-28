package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

@RunWith(AndroidJUnit4.class)
public class BaseItemTest {

    @Test
    public void testCollision(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        float[] tmpRot = new float[16];
        Matrix.setIdentityM(tmpRot, 0);
        Rocket r = new Rocket(appContext, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f}, tmpRot, 2f);
        r.move();

        Icosahedron i = new Icosahedron(appContext, new float[]{0f, 0f, 0.2f}, new Random());
        i.move();

        assertTrue(i.isCollided(r));
        assertTrue(r.isCollided(i));

        i = new Icosahedron(appContext, new float[]{0f, 0f, 20f}, new Random());
        i.move();

        assertFalse(i.isCollided(r));
        assertFalse(r.isCollided(i));
    }
}
