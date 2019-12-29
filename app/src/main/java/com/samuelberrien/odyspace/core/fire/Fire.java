package com.samuelberrien.odyspace.core.fire;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.baseitem.BaseItem;
import com.samuelberrien.odyspace.core.collision.CollisionMesh;
import com.samuelberrien.odyspace.drawable.ObjModelMtlVBO;

import java.util.List;

public abstract class Fire {

	ObjModelMtlVBO ammo;

	protected CollisionMesh collisionMesh;

	protected Context glContext;

	public Fire(Context glContext, String objFileName, String mtlFileName) {
		this.glContext = glContext;
		ammo = new ObjModelMtlVBO(glContext, objFileName, mtlFileName, 2f, 0f, false);
		collisionMesh = new CollisionMesh(glContext, objFileName);
	}

	public abstract void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets);
}
