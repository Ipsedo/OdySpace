package com.samuelberrien.odyspace.core.fire;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.objects.BaseItem;
import com.samuelberrien.odyspace.core.objects.ammos.RocketItem;

import java.util.List;

public class SimpleFire extends Fire {

	SimpleFire(Context glContext) {
		super(glContext, "obj/rocket.obj", "obj/rocket.mtl");
	}

	@Override
	public void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets) {
		rockets.add(new RocketItem(glContext, ammo, collisionMesh, position.clone(), originalSpeedVec.clone(), rotationMatrix.clone(), maxSpeed));
	}

}
