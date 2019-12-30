package com.samuelberrien.odyspace.ui.infos;

import android.content.Context;

import com.samuelberrien.odyspace.R;

public class Bonus3DView extends Item3DView {

	public Bonus3DView(Context context, String name) {
		super(context, name);
	}

	@Override
	public void changeObj(String name) {
		if (name.equals(getContext().getString(R.string.bonus_1))) {
			objFileName = "obj/arrow_speed.obj";
			mtlFileName = "obj/arrow_speed.mtl";
		} else if (name.equals(getContext().getString(R.string.bought_duration))) {
			objFileName = "obj/clock.obj";
			mtlFileName = "obj/clock.mtl";
		} else if (name.equals(getContext().getString(R.string.bonus_2))) {
			objFileName = "obj/shield.obj";
			mtlFileName = "obj/shield.mtl";
		}

		willCreateObj = true;
	}
}
