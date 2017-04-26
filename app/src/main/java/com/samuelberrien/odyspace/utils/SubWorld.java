package com.samuelberrien.odyspace.utils;

import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.objects.Icosahedron;
import com.samuelberrien.odyspace.objects.Rocket;

import java.util.ArrayList;

/**
 * Created by samuel on 26/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class SubWorld {

    private LevelLimits levelLimits;

    private SubWorld father;

    private ArrayList<Rocket> amis;
    private ArrayList<Icosahedron> ennemis;

    private float limitSize;

    public SubWorld(LevelLimits levelLimits, SubWorld father, ArrayList<Rocket> amis, ArrayList<Icosahedron> ennemis, float limitSize){
        this.levelLimits = levelLimits;
        this.father = father;
        this.amis = amis;
        this.ennemis = ennemis;
        this.limitSize = limitSize;
    }

    public SubWorld[] makeSons(){
        SubWorld[] sons = new SubWorld[8];
        LevelLimits[] levelLimitsSons = this.levelLimits.makeOctSons();
        ArrayList<Rocket>[] futurAmis = new ArrayList[8];
        ArrayList<Icosahedron>[] futurEnnemis = new ArrayList[8];

        for(int i = 0; i < 8; i++){
            futurAmis[i] = new ArrayList<>();
            futurEnnemis[i] = new ArrayList<>();
        }

        for(int i = 0; i < this.amis.size(); i++){
            for(int j = 0; j < 8; j++){
                if(!this.amis.get(i).isOutOfBound(levelLimitsSons[j])){
                    futurAmis[j].add(this.amis.get(i));
                    //this.amis.remove(i);
                }
            }
        }

        for(int i = 0; i < this.ennemis.size(); i++){
            for(int j = 0; j < 8; j++){
                if(!this.ennemis.get(i).isOutOfBound(levelLimitsSons[j])){
                    futurEnnemis[j].add(this.ennemis.get(i));
                    //this.ennemis.remove(i);
                }
            }
        }

        for(int i = 0; i < 8; i++){
            sons[i] = new SubWorld(levelLimitsSons[i], this, futurAmis[i], futurEnnemis[i], this.limitSize);
        }

        return sons;
    }

    public void computeCollision(){
        for(int i = 0; i < this.ennemis.size(); i++){
            for(int j = 0; j < this.amis.size(); j++){
                if(this.ennemis.get(i).isCollided(this.amis.get(j))){
                    this.ennemis.get(i).decrementsBothLife(this.amis.get(j));
                }
            }
        }
    }

    public boolean isLeaf(){
        return this.levelLimits.getSizeLength() <= this.limitSize;
    }

    public boolean containsNoCollision(){
        return this.amis.isEmpty() || this.ennemis.isEmpty();
    }
}