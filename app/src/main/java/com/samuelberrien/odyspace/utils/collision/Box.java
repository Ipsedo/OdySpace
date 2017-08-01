package com.samuelberrien.odyspace.utils.collision;

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
		this.bounds = new float[2][3];
		this.bounds[0][0] = this.x;
		this.bounds[0][1] = this.y;
		this.bounds[0][2] = this.z;
		this.bounds[1][0] = this.x + this.sizeX;
		this.bounds[1][1] = this.y + this.sizeY;
		this.bounds[1][2] = this.z + this.sizeZ;
	}

	private boolean checkIntersectionInclusion(Box b) {
		return this.x + this.sizeX > b.x && this.x < b.x + b.sizeX && this.y + this.sizeY > b.y && this.y < b.y + b.sizeY && this.z + this.sizeZ > b.z && this.z < b.z + b.sizeZ;
	}

	public boolean isInside(Box b) {
		/*//check the X axis
		if (Math.abs(this.x - b.x) < this.sizeX + b.sizeX) {
            //check the Y axis
            if (Math.abs(this.y - b.y) < this.sizeY + b.sizeY) {
                //check the Z axis
                if (Math.abs(this.z - b.z) < this.sizeZ + b.sizeZ) {
                    return true;
                }
            }
        }*/

		return this.checkIntersectionInclusion(b) || b.checkIntersectionInclusion(this);
		//return Math.abs(this.x - b.x) * 2f < this.sizeX + b.sizeX && Math.abs(this.y - b.y) * 2f < this.sizeY + b.sizeY && Math.abs(this.z - b.z) * 2f < this.sizeZ + b.sizeZ;
	}

	public boolean rayIntersect(Ray r) {
		float tmin, tmax, tymin, tymax, tzmin, tzmax;

		tmin = (this.bounds[r.signGet(0)][0] - r.originGet(0)) * r.invDirGet(0);
		tmax = (this.bounds[1 - r.signGet(0)][0] - r.originGet(0)) * r.invDirGet(0);
		tymin = (this.bounds[r.signGet(1)][1] - r.originGet(1)) * r.invDirGet(1);
		tymax = (this.bounds[1 - r.signGet(1)][1] - r.originGet(1)) * r.invDirGet(1);

		if ((tmin > tymax) || (tymin > tmax))
			return false;
		if (tymin > tmin)
			tmin = tymin;
		if (tymax < tmax)
			tmax = tymax;

		tzmin = (this.bounds[r.signGet(2)][2] - r.originGet(2)) * r.invDirGet(2);
		tzmax = (this.bounds[1 - r.signGet(2)][2] - r.originGet(2)) * r.invDirGet(2);

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
		float a = this.x + this.sizeX * 0.5f;
		float b = this.y + this.sizeY * 0.5f;
		float c = this.z + this.sizeZ * 0.5f;
		float newSizeX = this.sizeX * 0.5f;
		float newSizeY = this.sizeY * 0.5f;
		float newSizeZ = this.sizeZ * 0.5f;
		sons[0] = new Box(this.x, this.y, this.z, newSizeX, newSizeY, newSizeZ);
		sons[1] = new Box(a, this.y, this.z, newSizeX, newSizeY, newSizeZ);
		sons[2] = new Box(a, this.y, c, newSizeX, newSizeY, newSizeZ);
		sons[3] = new Box(this.x, this.y, c, newSizeX, newSizeY, newSizeZ);

		sons[4] = new Box(this.x, b, this.z, newSizeX, newSizeY, newSizeZ);
		sons[5] = new Box(a, b, this.z, newSizeX, newSizeY, newSizeZ);
		sons[6] = new Box(a, b, c, newSizeX, newSizeY, newSizeZ);
		sons[7] = new Box(this.x, b, c, newSizeX, newSizeY, newSizeZ);
		return sons;
	}

	public float getSizeAv() {
		return (this.sizeX + this.sizeY + this.sizeZ) * 0.3f;
	}

	public float[] getPos() {
		return new float[]{this.x + this.sizeX * 0.5f, this.y + this.sizeY * 0.5f, this.z + this.sizeZ * 0.5f};
	}

	@Override
	public String toString() {
		return "Box : { x = " + this.x + ", y = " + this.y + ", z = " + this.z + " ; sX = " + this.sizeX + ", sY = " + this.sizeY + ", sZ = " + this.sizeZ + " }";
	}
}
