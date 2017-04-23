package com.samuelberrien.odyspace.utils;

import com.samuelberrien.odyspace.utils.Triangle;

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
        double[] u0 = new double[]{-1.2d, -1.9, -1.3};
        double[] u1 = new double[]{-0.9d, -3, -0.8};
        double[] u2 = new double[]{-3, -2, -1.5};

        double[] v0 = new double[]{1.2d, 1.9, 1.3};
        double[] v1 = new double[]{0.9d, 3, 0.8};
        double[] v2 = new double[]{3, 2, 1.5};

        assertTrue(Triangle.tr_tri_intersect3D(u0, u1, u2, v0, v1, v2) == 0);

        assertTrue(Triangle.tr_tri_intersect3D(u0.clone(), u1.clone(), u2.clone(), u0.clone(), v1.clone(), v2.clone()) > 0);
    }
}
