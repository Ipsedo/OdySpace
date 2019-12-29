package com.samuelberrien.odyspace.core.fire;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.objects.BaseItem;
import com.samuelberrien.odyspace.core.objects.ammos.BombItem;

import java.util.List;

public class SimpleBomb extends Fire {
	public SimpleBomb(Context glContext) {
		super(glContext, "obj/bomb.obj", "obj/bomb.mtl");
	}

	@Override
	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets) {
		rockets.add(new BombItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed));
	}
}
