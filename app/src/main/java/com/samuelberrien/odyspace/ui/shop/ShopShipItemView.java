package com.samuelberrien.odyspace.ui.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.core.Purchases;
import com.samuelberrien.odyspace.ui.infos.Item3DView;
import com.samuelberrien.odyspace.ui.infos.Ship3DView;

public class ShopShipItemView extends ShopItemView {

	public ShopShipItemView(Context context, int indexItem) {
		super(context, indexItem);
	}

	@Override
	protected void makeItem3D() {
		String[] names = getResources().getStringArray(R.array.ship_shop_list_item);
		item3DView = new Ship3DView(getContext(), names[index]);
	}

	@Override
	protected void setText() {
		String[] items = getResources().getStringArray(R.array.ship_shop_list_item);
		int[] prices = getResources().getIntArray(R.array.ship_shop_price);

		int[] lifes = getResources().getIntArray(R.array.ship_life_shop_list_item);
		int life = lifes[index];
		int cost = prices[index];
		infos.setText(getResources().getString(R.string.ship_info_shop, items[index], life, cost));
	}

	@Override
	protected void updateButton() {
		final String[] items = getResources().getStringArray(R.array.ship_shop_list_item);
		final int[] prices = getResources().getIntArray(R.array.ship_shop_price);
		final SharedPreferences.Editor editor = savedShop.edit();

		buyButton.setOnClickListener((view) -> {
			int currMoney = savedShop.getInt(
					getContext().getString(R.string.saved_money),
					getResources().getInteger(R.integer.saved_init_money));
			if (currMoney >= prices[index]) {
				editor.putBoolean(items[index], true);
				editor.putInt(
						getContext().getString(R.string.saved_money),
						currMoney - prices[index]);
				editor.apply();
				buyButton.setClickable(false);
				buyButton.setBackground(
						ContextCompat.getDrawable(getContext(),
								R.drawable.button_pressed));
			}

		});
		int rBool = items[index].equals(getContext().getString(R.string.ship_simple)) ?
				R.bool.vrai : R.bool.faux;
		if (savedShop.getBoolean(items[index], getResources().getBoolean(rBool))) {
			buyButton.setClickable(false);
			buyButton.setBackground(
					ContextCompat.getDrawable(getContext(),
							R.drawable.button_pressed));
		} else {
			buyButton.setClickable(true);
		}
	}
}
