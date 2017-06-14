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

    public Box(float x, float y, float z, float sizeX, float sizeY, float sizeZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public boolean isInside(Box b) {
        //check the X axis
        if (Math.abs(this.x - b.x) < this.sizeX + b.sizeX) {
            //check the Y axis
            if (Math.abs(this.y - b.y) < this.sizeY + b.sizeY) {
                //check the Z axis
                if (Math.abs(this.z - b.z) < this.sizeZ + b.sizeZ) {
                    return true;
                }
            }
        }

        return false;
    }

    public Box[] makeSons() {
        Box[] sons = new Box[8];
        float a = this.x + this.sizeX / 2f;
        float b = this.y + this.sizeY / 2f;
        float c = this.z + this.sizeZ / 2f;
        sons[0] = new Box(this.x, this.y, this.z, this.sizeX / 2f, this.sizeY / 2f, this.sizeZ / 2f);
        sons[1] = new Box(a, this.y, this.z, this.sizeX / 2f, this.sizeY / 2f, this.sizeZ / 2f);
        sons[2] = new Box(a, this.y, c, this.sizeX / 2f, this.sizeY / 2f, this.sizeZ / 2f);
        sons[3] = new Box(this.x, this.y, c, this.sizeX / 2f, this.sizeY / 2f, this.sizeZ / 2f);

        sons[4] = new Box(this.x, b, this.z, this.sizeX / 2f, this.sizeY / 2f, this.sizeZ / 2f);
        sons[5] = new Box(a, b, this.z, this.sizeX / 2f, this.sizeY / 2f, this.sizeZ / 2f);
        sons[6] = new Box(a, b, c, this.sizeX / 2f, this.sizeY / 2f, this.sizeZ / 2f);
        sons[7] = new Box(this.x, b, c, this.sizeX / 2f, this.sizeY / 2f, this.sizeZ / 2f);
        return sons;
    }

    public float getSizeAv() {
        return (this.sizeX + this.sizeY + this.sizeZ) / 3f;
    }
}
