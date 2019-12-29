package com.samuelberrien.odyspace.core.fire;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.objects.BaseItem;
import com.samuelberrien.odyspace.core.objects.ammos.GuidedMissileItem;

import java.util.List;

public class GuidedMissile extends Fire {

	public GuidedMissile(Context glContext) {
		super(glContext, "obj/rocket.obj", "obj/rocket.mtl");
	}

	@Override
	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets) {
		rockets.add(new GuidedMissileItem(glContext, ammo, crashableMesh, position.clone(), originalSpeedVec, rotationMatrix, maxSpeed, targets[0]));
	}
}
