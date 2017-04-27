package com.samuelberrien.odyspace.utils;

import com.samuelberrien.odyspace.utils.collision.Triangle;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by samuel on 24/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class TestTriangle {

    @Test
    public void test() {
        float[] u0 = new float[]{-1.2f, -1.9f, -1.3f};
        float[] u1 = new float[]{-0.9f, -3, -0.8f};
        float[] u2 = new float[]{-3, -2, -1.5f};

        float[] v0 = new float[]{1.2f, 1.9f, 1.3f};
        float[] v1 = new float[]{0.9f, 3, 0.8f};
        float[] v2 = new float[]{3, 2, 1.5f};

        assertTrue(Triangle.tr_tri_intersect3D(u0.clone(), u1.clone(), u2.clone(), v0.clone(), v1.clone(), v2.clone()) == 0);
        assertTrue(Triangle.tr_tri_intersect3D(v0.clone(), v1.clone(), v2.clone(), u0.clone(), u1.clone(), u2.clone()) == 0);

        u0 = new float[]{0f, 0f, 0f};
        u1 = new float[]{0f, 1f, 0f};
        u2 = new float[]{1f, 0f, 0f};

        v0 = new float[]{0.1f, 0.1f, 0f};
        v1 = new float[]{0.1f, 1.1f, 0f};
        v2 = new float[]{1.1f, 0.1f, 0f};

        assertTrue(Triangle.tr_tri_intersect3D(v0.clone(), v1.clone(), v2.clone(), u0.clone(), u1.clone(), u2.clone()) > 0 && Triangle.tr_tri_intersect3D(u0.clone(), u1.clone(), u2.clone(), v0.clone(), v1.clone(), v2.clone()) > 0);

        u0 = new float[]{0f, 0f, 1f};
        u1 = new float[]{4f, -1f, 0f};
        u2 = new float[]{-1f, 5f, 0f};

        v0 = new float[]{0f, 0f, -2f};
        v1 = new float[]{0f, 0f, 2f};
        v2 = new float[]{3f, 4f, 0f};

        assertTrue(Triangle.tr_tri_intersect3D(v0.clone(), v1.clone(), v2.clone(), u0.clone(), u1.clone(), u2.clone()) > 0 && Triangle.tr_tri_intersect3D(u0.clone(), u1.clone(), u2.clone(), v0.clone(), v1.clone(), v2.clone()) > 0);
    }
}
