package com.samuelberrien.odyspace.utils.collision;

import com.samuelberrien.odyspace.objects.BaseItem;
import com.samuelberrien.odyspace.utils.game.LevelLimits;

import java.util.ArrayList;

/**
 * Created by samuel on 26/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Octree {

    private LevelLimits levelLimits;

    private Octree father;

    private ArrayList<BaseItem> amis;
    private ArrayList<BaseItem> ennemis;

    private float limitSize;

    public Octree(LevelLimits levelLimits, Octree father, ArrayList<BaseItem> amis, ArrayList<BaseItem> ennemis, float limitSize) {
        this.levelLimits = levelLimits;
        this.father = father;
        this.amis = amis;
        this.ennemis = ennemis;
        this.limitSize = limitSize;
    }

    private Octree[] makeSons() {
        Octree[] sons = new Octree[8];
        LevelLimits[] levelLimitsSons = this.levelLimits.makeOctSons();
        ArrayList<BaseItem>[] futurAmis = new ArrayList[8];
        ArrayList<BaseItem>[] futurEnnemis = new ArrayList[8];

        for (int i = 0; i < 8; i++) {
            futurAmis[i] = new ArrayList<>();
            futurEnnemis[i] = new ArrayList<>();

            for (BaseItem j : this.amis)
                if (!j.isOutOfBound(levelLimitsSons[i]))
                    futurAmis[i].add(j);
            for (BaseItem j : this.ennemis)
                if (!j.isOutOfBound(levelLimitsSons[i]))
                    futurEnnemis[i].add(j);

            sons[i] = new Octree(levelLimitsSons[i], this, futurAmis[i], futurEnnemis[i], this.limitSize);
        }

        return sons;
    }

    private void computeCollision() {
        for (BaseItem i : this.ennemis)
            for (BaseItem j : this.amis)
                if (i.isCollided(j))
                    i.decrementsBothLife(j);
    }

    public void computeOctree() {
        if (this.isLeaf()) {
            this.computeCollision();
        } else {
            for (Octree sb : this.makeSons()) {
                if (!sb.containsNoCollision()) {
                    sb.computeOctree();
                }
            }
        }
    }

    private boolean isLeaf() {
        return this.levelLimits.getSizeLength() <= this.limitSize;
    }

    private boolean containsNoCollision() {
        return this.amis.isEmpty() || this.ennemis.isEmpty();
    }
}