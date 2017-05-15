package com.samuelberrien.odyspace.objects;

import android.content.Context;
import android.opengl.Matrix;

import com.samuelberrien.odyspace.drawable.Explosion;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;
import com.samuelberrien.odyspace.utils.game.Fire;
import com.samuelberrien.odyspace.utils.maths.Vector;

import java.util.List;
import java.util.Random;

/**
 * Created by samuel on 15/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Turret extends BaseItem {

    private Random rand;

    private ObjModelMtlVBO rocket;

    private Explosion explosion;

    public Turret(Context context, float[] mPosition) {
        super(context, "turret.obj", "turret.mtl", 1f, 0f, false, 1, mPosition, new float[3], new float[3], 2f);

        this.rand = new Random(System.currentTimeMillis());
        this.rocket = new ObjModelMtlVBO(context, "rocket.obj", "rocket.mtl", 1f, 0f, false);
    }

    public void makeExplosion(Context context) {
        this.explosion = new Explosion(context, super.mPosition.clone(), super.diffColorBuffer, 1.5f, 0.05f);
    }

    public void addExplosion(List<Explosion> explosions) {
        explosions.add(this.explosion);
    }

    public void fire(List<BaseItem> rockets, Ship ship) {
        if (this.rand.nextInt(800) == 50) {
            float[] speedVec = Vector.normalize3f(new float[]{ship.mPosition[0] - super.mPosition[0], ship.mPosition[1] - super.mPosition[1], ship.mPosition[2] - super.mPosition[2]});
            float[] originaleVec = new float[]{0f, 0f, 1f};
            float angle = (float) (Math.acos(Vector.dot3f(speedVec, originaleVec)) * 360d / (Math.PI * 2d));
            float[] rotAxis = Vector.cross3f(originaleVec, speedVec);
            float[] tmpMat = new float[16];
            Matrix.setRotateM(tmpMat, 0, angle, rotAxis[0], rotAxis[1], rotAxis[2]);
            Fire.fire(this.rocket, rockets, Fire.Type.SIMPLE_FIRE, super.mPosition.clone(), originaleVec, tmpMat, 0.01f);
        }
    }

    public void move(Ship ship) {
        float[] u = new float[]{ship.mPosition[0]- super.mPosition[0], 0f, ship.mPosition[2]- super.mPosition[2]};
        float[] v = new float[]{0f, 0f, 1f};

        float[] cross = Vector.normalize3f(Vector.cross3f(Vector.normalize3f(u), Vector.normalize3f(v)));
        double angle = Math.acos(Vector.dot3f(Vector.normalize3f(u), Vector.normalize3f(v)));

        Matrix.setRotateM(super.mRotationMatrix, 0, (float) Math.toDegrees(angle), cross[0], cross[1], cross[2]);

        float[] mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, super.mPosition[0], super.mPosition[1], super.mPosition[2]);
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, super.mRotationMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, this.scale, this.scale, this.scale);

        super.mModelMatrix = mModelMatrix.clone();
    }

    @Override
    public void move() {

    }
}
