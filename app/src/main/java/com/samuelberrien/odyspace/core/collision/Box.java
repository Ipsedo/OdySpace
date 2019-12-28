package com.samuelberrien.odyspace.core.collision;

/**
 * Created by samuel on 13/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Box {

	private float x;
	private float y;
	private float z;

	private float sizeX;
	private float sizeY;
	private float sizeZ;

	private float[][] bounds;

	public Box(float x, float y, float z, float sizeX, float sizeY, float sizeZ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		bounds = new float[2][3];
		bounds[0][0] = this.x;
		bounds[0][1] = this.y;
		bounds[0][2] = this.z;
		bounds[1][0] = this.x + this.sizeX;
		bounds[1][1] = this.y + this.sizeY;
		bounds[1][2] = this.z + this.sizeZ;
	}

	private boolean checkIntersectionInclusion(Box b) {
		return x + sizeX > b.x && x < b.x + b.sizeX && y + sizeY > b.y && y < b.y + b.sizeY && z + sizeZ > b.z && z < b.z + b.sizeZ;
	}

	public boolean isInside(Box b) {
		/*//check the X axis
		if (Math.abs(x - b.x) < sizeX + b.sizeX) {
            //check the Y axis
            if (Math.abs(y - b.y) < sizeY + b.sizeY) {
                //check the Z axis
                if (Math.abs(z - b.z) < sizeZ + b.sizeZ) {
                    return true;
                }
            }
        }*/
		return checkIntersectionInclusion(b) || b.checkIntersectionInclusion(this);
		//return Math.abs(x - b.x) * 2f < sizeX + b.sizeX && Math.abs(y - b.y) * 2f < sizeY + b.sizeY && Math.abs(z - b.z) * 2f < sizeZ + b.sizeZ;
	}

	public boolean rayIntersect(Ray r) {
		float tmin, tmax, tymin, tymax, tzmin, tzmax;

		tmin = (bounds[r.signGet(0)][0] - r.originGet(0)) * r.invDirGet(0);
		tmax = (bounds[1 - r.signGet(0)][0] - r.originGet(0)) * r.invDirGet(0);
		tymin = (bounds[r.signGet(1)][1] - r.originGet(1)) * r.invDirGet(1);
		tymax = (bounds[1 - r.signGet(1)][1] - r.originGet(1)) * r.invDirGet(1);

		if ((tmin > tymax) || (tymin > tmax))
			return false;
		if (tymin > tmin)
			tmin = tymin;
		if (tymax < tmax)
			tmax = tymax;

		tzmin = (bounds[r.signGet(2)][2] - r.originGet(2)) * r.invDirGet(2);
		tzmax = (bounds[1 - r.signGet(2)][2] - r.originGet(2)) * r.invDirGet(2);

		if ((tmin > tzmax) || (tzmin > tmax))
			return false;
		/*if (tzmin > tmin)
			tmin = tzmin;
		if (tzmax < tmax)
			tmax = tzmax;*/

		return true;
	}

	public Box[] makeSons() {
		Box[] sons = new Box[8];
		float a = x + sizeX * 0.5f;
		float b = y + sizeY * 0.5f;
		float c = z + sizeZ * 0.5f;
		float newSizeX = sizeX * 0.5f;
		float newSizeY = sizeY * 0.5f;
		float newSizeZ = sizeZ * 0.5f;
		sons[0] = new Box(x, y, z, newSizeX, newSizeY, newSizeZ);
		sons[1] = new Box(a, y, z, newSizeX, newSizeY, newSizeZ);
		sons[2] = new Box(a, y, c, newSizeX, newSizeY, newSizeZ);
		sons[3] = new Box(x, y, c, newSizeX, newSizeY, newSizeZ);

		sons[4] = new Box(x, b, z, newSizeX, newSizeY, newSizeZ);
		sons[5] = new Box(a, b, z, newSizeX, newSizeY, newSizeZ);
		sons[6] = new Box(a, b, c, newSizeX, newSizeY, newSizeZ);
		sons[7] = new Box(x, b, c, newSizeX, newSizeY, newSizeZ);
		return sons;
	}

	public float getSizeAv() {
		return (sizeX + sizeY + sizeZ) * 0.3f;
	}

	public float[] getPos() {
		return new float[]{x + sizeX * 0.5f, y + sizeY * 0.5f, z + sizeZ * 0.5f};
	}

	@Override
	public String toString() {
		return "Box : { x = " + x + ", y = " + y + ", z = " + z + " ; sX = " + sizeX + ", sY = " + sizeY + ", sZ = " + sizeZ + " }";
	}
}
