package com.samuelberrien.odyspace.ui.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.ui.infos.Item3DView;

/**
 * Created by samuel on 13/10/17.
 */

public abstract class ShopItemView extends LinearLayout {

	protected Item3DView item3DView;

	protected TextView infos;

	protected int index;

	protected SharedPreferences savedShop;

	protected Button buyButton;

	public ShopItemView(Context context, int indexItem) {
		super(context);
		setOrientation(HORIZONTAL);

		index = indexItem;
		savedShop = context.getSharedPreferences(context.getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		infos = new TextView(context);
		infos.setGravity(Gravity.CENTER);
		buyButton = new Button(context);
		buyButton.setText(getContext().getString(R.string.buy));
		buyButton.setBackground(ContextCompat.getDrawable(context, R.drawable.drawer_button));

		makeItem3D();

		setText();

		updateButton();

		LayoutParams layoutParams = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				1, r.getDisplayMetrics());
		layoutParams.setMargins(5, 5, 5, 5 + (int) px);

		addView(item3DView, layoutParams);
		addView(infos, layoutParams);

		layoutParams = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		addView(buyButton, layoutParams);

		setBackground(ContextCompat.getDrawable(context, R.drawable.button_unpressed));
	}

	protected abstract void makeItem3D();

	protected abstract void setText();

	protected abstract void updateButton();
}
