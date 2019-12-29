package com.samuelberrien.odyspace.core.fire;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.baseitem.ammos.BombItem;

import java.util.List;

public class SimpleBomb extends Fire {

	SimpleBomb(Context glContext) {
		super(glContext, "obj/bomb.obj", "obj/bomb.mtl");
	}

	@Override
	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets) {
		rockets.add(new BombItem(glContext, ammo, collisionMesh, position.clone(), originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed));
	}
}
