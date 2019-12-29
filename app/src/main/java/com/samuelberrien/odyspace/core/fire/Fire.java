package com.samuelberrien.odyspace.core.fire;

import android.content.Context;

import com.samuelberrien.odyspace.core.Item;
import com.samuelberrien.odyspace.core.objects.BaseItem;
import com.samuelberrien.odyspace.core.objects.CrashableMesh;
import com.samuelberrien.odyspace.drawable.obj.ObjModelMtlVBO;

import java.util.List;

public abstract class Fire {

	ObjModelMtlVBO ammo;

	protected CrashableMesh crashableMesh;

	protected Context glContext;

	public Fire(Context glContext, String objFileName, String mtlFileName) {
		this.glContext = glContext;
		ammo = new ObjModelMtlVBO(glContext, objFileName, mtlFileName, 2f, 0f, false);
		crashableMesh = new CrashableMesh(glContext, objFileName);
	}

	public abstract void fire(List<BaseItem> rockets, float[] position, float[] originalSpeedVec, float[] rotationMatrix, float maxSpeed, Item... targets);
}
