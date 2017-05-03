package com.samuelberrien.odyspace.utils.game;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtl;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Rocket;

import java.util.ArrayList;

/**
 * Created by samuel on 03/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Fire {

    public enum Type {
        FIRST, SECOND
    }

    public static void fire(ObjModelMtl rocketModel, ArrayList<BaseItem> rockets, Fire.Type type, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed) {
        switch (type) {
            case FIRST:
                rockets.add(new Rocket(rocketModel, position, originalSpeedVec, new float[]{0f, 0f, 0f}, rotationMatrix, maxSpeed));
                break;
            case SECOND:
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, rotationMatrix.clone(), maxSpeed));

                float[] tmpMat = new float[16];
                Matrix.setRotateM(tmpMat, 0, 2.5f, 1f, 0f, 0f);
                float[] res = new float[16];
                Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed));

                Matrix.setRotateM(tmpMat, 0, -2.5f, 1f, 0f, 0f);
                res = new float[16];
                Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed));

                Matrix.setRotateM(tmpMat, 0, -2.5f, 0f, 1f, 0f);
                res = new float[16];
                Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed));

                Matrix.setRotateM(tmpMat, 0, 2.5f, 0f, 1f, 0f);
                res = new float[16];
                Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed));
                break;
        }
    }
}
