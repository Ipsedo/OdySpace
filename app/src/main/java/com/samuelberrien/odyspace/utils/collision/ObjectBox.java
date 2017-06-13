package com.samuelberrien.odyspace.utils.collision;

/**
 * Created by samuel on 13/06/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ObjectBox {

    private float x;
    private float y;
    private float z;

    private float sizeX;
    private float sizeY;
    private float sizeZ;

    public ObjectBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public boolean checkCollision(ObjectBox b) {
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
}
