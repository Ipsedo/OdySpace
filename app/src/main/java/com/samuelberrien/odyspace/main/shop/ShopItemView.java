package com.samuelberrien.odyspace.main.shop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.samuelberrien.odyspace.main.infos.Item3DWindow;

/**
 * Created by samuel on 13/10/17.
 */

public class ShopItemView extends LinearLayout {

	private View item3DWindow;
	private LayoutParams layoutParams;

	public enum Kind { SHIP, FIRE, BONUS }

	public ShopItemView(Context context, Kind kind, String name) {
		super(context);
		setOrientation(HORIZONTAL);
		layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;

		switch (kind) {
			case SHIP:
				item3DWindow = Item3DWindow.makeShipView(context, name);
				break;
			case FIRE:
				item3DWindow = Item3DWindow.makeFireView(context, name);
				break;
			case BONUS:
				item3DWindow = new View(context);
				break;
		}
	}
}
