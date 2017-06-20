package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.game.BossMove;
import com.samuelberrien.odyspace.utils.game.FireType;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.List;

/**
 * Created by samuel on 30/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Boss extends BaseItem {

    private Context context;

    private final int MAX_COUNT = 200;
    private int counter;

    private ObjModelMtlVBO rocket;
    private FireType fireType;
    private BossMove bossMoveType;

    private int colorCounter;
    private boolean changingColor;

    public Boss(Context context, String objFileName, String mtlFileName, int life, float[] mPosition, float scale, FireType fireType, BossMove bossMoveType) {
        super(context, objFileName, mtlFileName, 1f, 0f, false, life, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f}, scale);
        this.context = context;
        this.counter = 0;
        this.rocket = new ObjModelMtlVBO(this.context, "rocket.obj", "rocket.mtl", 2f, 0f, false);
        this.fireType = fireType;
        this.colorCounter = 0;
        this.changingColor = false;
        this.bossMoveType = bossMoveType;
        this.mediaPlayer = MediaPlayer.create(this.context, R.raw.big_boom);
    }

    @Override
    public int getDamage() {
        return Integer.MAX_VALUE - 1;
    }

    @Override
    public void decrementLife(int minus) {
        if (minus > 0 && !this.changingColor) {
            this.changingColor = true;
            super.changeColor();
        }
        super.life = super.life - minus >= 0 ? super.life - minus : 0;
    }

    private void count() {
        this.counter = (this.counter > this.MAX_COUNT ? 0 : this.counter + 1);
        if (this.changingColor && this.colorCounter > 75) {
            super.changeColor();
            this.changingColor = false;
            this.colorCounter = 0;
        } else if (this.changingColor) {
            this.colorCounter++;
        }
    }


        /*if (this.counter == this.MAX_COUNT / 2) {
            float[] shipBossVec = Vector.normalize3f(new float[]{ship.mPosition[0] - super.mPosition[0], ship.mPosition[0] - super.mPosition[0], ship.mPosition[0] - super.mPosition[0]});
            super.mSpeed[0] = this.maxSpeed * shipBossVec[0];
            super.mSpeed[1] = this.maxSpeed * shipBossVec[1];
            super.mSpeed[2] = this.maxSpeed * shipBossVec[2];
        } else {
            this.phi += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
            this.theta += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
            super.mSpeed[0] = this.maxSpeed * (float) (Math.cos(phi) * Math.sin(theta));
            super.mSpeed[1] = this.maxSpeed * (float) Math.sin(phi);
            super.mSpeed[2] = this.maxSpeed * (float) (Math.cos(phi) * Math.cos(theta));
        }

        super.mPosition[0] += super.mSpeed[0];
        super.mPosition[1] += super.mSpeed[1];
        super.mPosition[2] += super.mSpeed[2];

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
        float[] vecToShip = Vector.normalize3f(new float[]{ship.mPosition[0] - super.mPosition[0], ship.mPosition[1] - super.mPosition[1], ship.mPosition[2] - super.mPosition[2]});
        float[] originaleVec = new float[]{0f, 0f, 1f};
        float angle = (float) (Math.acos(Vector.dot3f(vecToShip, originaleVec)) * 360d / (Math.PI * 2d));
        Matrix.setRotateM(super.mRotationMatrix, 0, angle, 0f, 1f, 0f);
        float[] tmpMat = mModelMatrix.clone();
        Matrix.multiplyMM(mModelMatrix, 0, tmpMat, 0, super.mRotationMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, super.scale, super.scale, super.scale);*/

    public void updateLifeProgress(ProgressBar progressBar) {
        progressBar.updateProgress(super.life);
    }

    public void fire(List<BaseItem> rockets, Ship ship) {
        if (this.counter % 101 == 0) {
            float[] speedVec = Vector.normalize3f(new float[]{ship.mPosition[0] - super.mPosition[0], ship.mPosition[1] - super.mPosition[1], ship.mPosition[2] - super.mPosition[2]});
            float[] originaleVec = new float[]{0f, 0f, 1f};
            float angle = (float) (Math.acos(Vector.dot3f(speedVec, originaleVec)) * 360d / (Math.PI * 2d));
            float[] rotAxis = Vector.cross3f(originaleVec, speedVec);
            float[] tmpMat = new float[16];
            Matrix.setRotateM(tmpMat, 0, angle, rotAxis[0], rotAxis[1], rotAxis[2]);
            this.fireType.fire(this.rocket, rockets, super.mPosition.clone(), originaleVec, tmpMat, 0.005f);
        }
    }

    @Override
    public void move() {
        super.mModelMatrix = this.bossMoveType.getModelMatrix(super.mPosition, super.mSpeed, super.scale);
        this.count();
    }
}
