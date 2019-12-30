package com.samuelberrien.odyspace.ui.infos;

import android.content.Context;

import com.samuelberrien.odyspace.R;

public class Ship3DView extends Item3DView {

	public Ship3DView(Context context, String name) {
		super(context, name);
	}

	@Override
	public void changeObj(String name) {
		if (name.equals(getContext().getString(R.string.ship_simple))) {
			objFileName = "obj/ship_3.obj";
			mtlFileName = "obj/ship_3.mtl";
		} else if (name.equals(getContext().getString(R.string.ship_bird))) {
			objFileName = "obj/ship_bird.obj";
			mtlFileName = "obj/ship_bird.mtl";
		} else if (name.equals(getContext().getString(R.string.ship_supreme))) {
			objFileName = "obj/ship_supreme.obj";
			mtlFileName = "obj/ship_supreme.mtl";
		} else if (name.equals(getContext().getString(R.string.bought_life))) {
			objFileName = "obj/heart.obj";
			mtlFileName = "obj/heart.mtl";
		} else if (name.equals(getContext().getString(R.string.ship_interceptor))) {
			objFileName = "obj/interceptor.obj";
			mtlFileName = "obj/interceptor.mtl";
		}

		willCreateObj = true;
	}
}
