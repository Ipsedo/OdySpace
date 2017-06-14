package com.samuelberrien.odyspace.utils.collision;

import com.samuelberrien.odyspace.utils.game.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samuel on 26/04/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class Octree {

    private Box boxes;

    private Octree father;

    private List<? extends Item> amis;
    private List<? extends Item> ennemis;

    private float limitSize;

    public Octree(Box boxes, Octree father, List<Item> amis, List<Item> ennemis, float limitSize) {
        this.boxes = boxes;
        this.father = father;
        this.amis = amis;
        this.ennemis = ennemis;
        this.limitSize = limitSize;
    }

    private Octree[] makeSons() {
        Octree[] sons = new Octree[8];
        Box[] levelLimitsSons = this.boxes.makeSons();
        ArrayList<Item>[] futurAmis = new ArrayList[8];
        ArrayList<Item>[] futurEnnemis = new ArrayList[8];

        for (int i = 0; i < 8; i++) {
            futurAmis[i] = new ArrayList<>();
            futurEnnemis[i] = new ArrayList<>();

            for (int j = this.amis.size() - 1; j >= 0; j--)
                if (this.amis.get(j).isInside(levelLimitsSons[i]))
                    futurAmis[i].add(this.amis.get(j));
            for (int j = this.ennemis.size() - 1; j >= 0; j--)
                if (this.ennemis.get(j).isInside(levelLimitsSons[i]))
                    futurEnnemis[i].add(this.ennemis.get(j));

            sons[i] = new Octree(levelLimitsSons[i], this, futurAmis[i], futurEnnemis[i], this.limitSize);
        }

        return sons;
    }

    private void computeCollision() {
        for (int i = this.ennemis.size() - 1; i >= 0; i--)
            for (int j = this.amis.size() - 1; j >= 0; j--) {
                if (this.ennemis.get(i).isCollided(this.amis.get(j))) {
                    int tmp = this.ennemis.get(i).getDamage();
                    this.ennemis.get(i).decrementLife(this.amis.get(j).getDamage());
                    this.amis.get(j).decrementLife(tmp);
                }
            }
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
        return this.boxes.getSizeAv() <= this.limitSize;
    }

    private boolean containsNoCollision() {
        return this.amis.isEmpty() || this.ennemis.isEmpty();
    }
}