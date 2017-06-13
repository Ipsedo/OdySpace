package com.samuelberrien.odyspace.utils.game;

import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Rocket;

import java.util.List;

/**
 * Created by samuel on 03/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public enum Fire {

    SIMPLE_FIRE, QUINT_FIRE, SIMPLE_BOMB;

    public  void fire(ObjModelMtlVBO rocketModel, List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed) {
        switch (this) {
            case SIMPLE_FIRE:
                rockets.add(new Rocket(rocketModel, position, originalSpeedVec, new float[]{0f, 0f, 0f}, rotationMatrix, maxSpeed, 1f, 1));
                break;
            case QUINT_FIRE:
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, rotationMatrix.clone(), maxSpeed, 1f, 1));

                float[] tmpMat = new float[16];
                Matrix.setRotateM(tmpMat, 0, 2.5f, 1f, 0f, 0f);
                float[] res = new float[16];
                Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));

                Matrix.setRotateM(tmpMat, 0, -2.5f, 1f, 0f, 0f);
                res = new float[16];
                Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));

                Matrix.setRotateM(tmpMat, 0, -2.5f, 0f, 1f, 0f);
                res = new float[16];
                Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));

                Matrix.setRotateM(tmpMat, 0, 2.5f, 0f, 1f, 0f);
                res = new float[16];
                Matrix.multiplyMM(res, 0, rotationMatrix, 0, tmpMat, 0);
                rockets.add(new Rocket(rocketModel, position.clone(), originalSpeedVec.clone(), new float[]{0f, 0f, 0f}, res.clone(), maxSpeed, 1f, 1));
                break;
            case SIMPLE_BOMB:
                rockets.add(new Rocket(rocketModel, position, originalSpeedVec, new float[]{0f, 0f, 0f}, rotationMatrix, maxSpeed, 2.5f, 3));
                break;
        }
    }
}
