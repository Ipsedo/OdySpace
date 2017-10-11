package com.samuelberrien.odyspace.objects.baseitem.shooters.boss;

import android.content.Context;

import com.samuelberrien.odyspace.drawable.ProgressBar;
import com.samuelberrien.odyspace.objects.baseitem.BaseItem;
import com.samuelberrien.odyspace.utils.game.FireType;
import com.samuelberrien.odyspace.utils.game.Shooter;

import java.util.List;

/**
 * Created by samuel on 30/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public abstract class Boss extends BaseItem implements Shooter {

	private final int MAX_COUNT = 200;
	private int counter;

	protected FireType fireType;
	protected List<BaseItem> rockets;

	private int colorCounter;
	private boolean changingColor;

	//TODO modèles simplifiés pr crashable ?
	public Boss(Context context, String objFileName, String mtlFileName, int life, float[] mPosition, float scale, FireType fireType, List<BaseItem> rockets) {
		super(context, objFileName, mtlFileName, objFileName, 1f, 0f, false, life, mPosition, new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f}, scale);
		counter = 0;
		this.fireType = fireType;
		colorCounter = 0;
		changingColor = false;
		this.rockets = rockets;
	}

	@Override
	public int getDamage() {
		return Integer.MAX_VALUE - 1;
	}

	@Override
	public void decrementLife(int minus) {
		if (minus > 0 && !changingColor) {
			changingColor = true;
			objModelMtlVBO.changeColor();
		}
		super.life = super.life - minus >= 0 ? super.life - minus : 0;
	}

	private void count() {
		counter = (counter > MAX_COUNT ? 0 : counter + 1);
		if (changingColor && colorCounter > 75) {
			objModelMtlVBO.changeColor();
			changingColor = false;
			colorCounter = 0;
		} else if (changingColor) {
			colorCounter++;
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

        /*
				this.phi += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
				this.theta += (this.rand.nextDouble() * 2d - 1d) / Math.PI;
				bossSpeed[0] = this.maxSpeed * (float) (Math.cos(phi) * Math.sin(theta));
				bossSpeed[1] = this.maxSpeed * (float) Math.sin(phi);
				bossSpeed[2] = this.maxSpeed * (float) (Math.cos(phi) * Math.cos(theta));

				bossPos[0] += bossSpeed[0];
				bossPos[1] += bossSpeed[1];
				bossPos[2] += bossSpeed[2];

				Matrix.translateM(mModelMatrix, 0, bossPos[0], bossPos[1], bossPos[2]);
				angle = (float) Math.toDegrees(Math.acos(Vector.dot3f(vecToShip, originaleVec)));
				Matrix.setRotateM(bossRotMat, 0, angle, 0f, 1f, 0f);
				Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix.clone(), 0, bossRotMat, 0);
				Matrix.scaleM(mModelMatrix, 0, scale, scale, scale);

        */

	public void updateLifeProgress(ProgressBar progressBar) {
		progressBar.updateProgress(super.maxLife - super.life);
	}

	@Override
	public abstract void fire();

	@Override
	public void update() {
		super.mModelMatrix = computeModelMatrix();
		count();
	}

	protected abstract float[] computeModelMatrix();
}
