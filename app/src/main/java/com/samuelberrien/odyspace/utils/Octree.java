package com.samuelberrien.odyspace.utils;

import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Icosahedron;
import com.samuelberrien.odyspace.objects.Rocket;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samuel on 26/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Octree {

    public static void computeOctree(LevelLimits levelLimits, ArrayList<Rocket> amis, ArrayList<Icosahedron> ennemis, float limitSize){
        Octree.recursiveCall(new SubWorld(levelLimits, null, amis, ennemis, limitSize));
    }

    private static void recursiveCall(SubWorld curr){
        if(curr.isLeaf()){
            curr.computeCollision();
        } else {
            for(SubWorld sb : curr.makeSons()){
                if(!sb.containsNoCollision()) {
                    Octree.recursiveCall(sb);
                }
            }
        }
    }
}
