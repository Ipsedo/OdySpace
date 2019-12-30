package com.samuelberrien.odyspace.ui.infos;

import android.content.Context;

import com.samuelberrien.odyspace.R;

public class Fire3DView extends Item3DView {

	public Fire3DView(Context context, String name) {
		super(context, name);
	}

	@Override
	public void changeObj(String name) {
		if (name.equals(getContext().getString(R.string.fire_1))) {
			objFileName = "obj/rocket.obj";
			mtlFileName = "obj/rocket.mtl";
		} else if (name.equals(getContext().getString(R.string.fire_2))) {
			objFileName = "obj/quint_fire.obj";
			mtlFileName = "obj/quint_fire.mtl";
		} else if (name.equals(getContext().getString(R.string.fire_3))) {
			objFileName = "obj/bomb.obj";
			mtlFileName = "obj/bomb.mtl";
		} else if (name.equals(getContext().getString(R.string.fire_4))) {
			objFileName = "obj/triple_fire.obj";
			mtlFileName = "obj/triple_fire.mtl";
		} else if (name.equals(getContext().getString(R.string.fire_5))) {
			objFileName = "obj/laser_item_menu.obj";
			mtlFileName = "obj/laser_item_menu.mtl";
		} else if (name.equals(getContext().getString(R.string.fire_6))) {
			objFileName = "obj/torus.obj";
			mtlFileName = "obj/torus.mtl";
		}
		willCreateObj = true;
	}
}
