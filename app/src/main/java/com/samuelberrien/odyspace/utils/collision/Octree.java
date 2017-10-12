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

	private List<Item> amis;
	private List<Item> ennemis;

	private float limitSize;

	public Octree(Box boxes, List<Item> amis, List<Item> ennemis, float limitSize) {
		this.boxes = boxes;
		this.amis = amis;
		this.ennemis = ennemis;
		this.limitSize = limitSize;
	}

	private Octree[] makeSons() {
		Octree[] sons = new Octree[8];
		Box[] levelLimitsSons = boxes.makeSons();
		/*ArrayList<Item>[] futurAmis = new ArrayList[8];
		ArrayList<Item>[] futurEnnemis = new ArrayList[8];*/

		for (int i = 0; i < sons.length; i++) {
			ArrayList<Item> futurAmis = new ArrayList<>();
			ArrayList<Item> futurEnnemis = new ArrayList<>();

			for (Item ami : amis)
				if (ami.isInside(levelLimitsSons[i]))
					futurAmis.add(ami);
			for (Item ennemi : ennemis)
				if (ennemi.isInside(levelLimitsSons[i]))
					futurEnnemis.add(ennemi);

			sons[i] = new Octree(levelLimitsSons[i], futurAmis, futurEnnemis, limitSize);
		}

		return sons;
	}

	private void computeCollision() {
		for (Item ami : amis)
			for (Item ennemi : ennemis)
				if (ami.isCollided(ennemi)) {
					int tmp = ami.getDamage();
					ami.decrementLife(ennemi.getDamage());
					ennemi.decrementLife(tmp);
				}
	}

	public void computeOctree() {
		if (isLeaf())
			computeCollision();
		else
			for (Octree sb : makeSons())
				if (!sb.containsNoCollision())
					sb.computeOctree();
	}

	private boolean isLeaf() {
		return boxes.getSizeAv() <= limitSize;
	}

	private boolean containsNoCollision() {
		return amis.isEmpty() || ennemis.isEmpty();
	}
}