package com.samuelberrien.odyspace.ui.infos;

import android.content.Context;

import com.samuelberrien.odyspace.R;

public class Enhancement3DView extends Item3DView {

	public Enhancement3DView(Context context, String name) {
		super(context, name);
	}

	@Override
	public void changeObj(String name) {
		if (name.equals(getContext().getString(R.string.bought_life))) {
			objFileName = "obj/heart.obj";
			mtlFileName = "obj/heart.mtl";
		} else if (name.equals(getContext().getString(R.string.bought_duration))) {
			objFileName = "obj/clock.obj";
			mtlFileName = "obj/clock.mtl";
		}
		willCreateObj = true;
	}
}
