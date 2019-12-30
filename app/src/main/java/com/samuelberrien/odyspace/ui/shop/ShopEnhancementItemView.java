package com.samuelberrien.odyspace.ui.shop;

import android.content.Context;
import android.content.SharedPreferences;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.ui.infos.Enhancement3DView;

public class ShopEnhancementItemView extends ShopItemView {

	public ShopEnhancementItemView(Context context, int indexItem) {
		super(context, indexItem);
	}

	@Override
	protected void makeItem3D() {
		String[] names = getResources().getStringArray(R.array.enhancement_shop_list_item);
		item3DView = new Enhancement3DView(getContext(), names[index]);
	}

	@Override
	protected void setText() {
		String[] items = getResources().getStringArray(R.array.enhancement_shop_list_item);
		int[] prices = getResources().getIntArray(R.array.enhancement_shop_price);

		int added = savedShop.getInt(items[index], getResources().getInteger(R.integer.zero));
		int cost = (int) (index == 0 ? Math.pow(added / 10., 2d) : Math.pow(added, 2d)) * prices[index];

		infos.setText(getResources().getString(R.string.enhancement_info_shop, items[index], added, cost));
	}

	@Override
	protected void updateButton() {
		final SharedPreferences.Editor editor = savedShop.edit();
		int[] prices = getResources().getIntArray(R.array.enhancement_shop_price);
		String[] items = getResources().getStringArray(R.array.enhancement_shop_list_item);

		buyButton.setOnClickListener((view) -> {

			int currMoney = savedShop.getInt(getContext().getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));

			int added = savedShop.getInt(items[index], getResources().getInteger(R.integer.zero));

			int cost = (int) (index == 0 ? Math.pow(added / 10., 2d) : Math.pow(added, 2d)) * prices[index];
			if (currMoney >= cost) {
				editor.putInt(items[index], added + (index == 0 ? 10 : 1));
				editor.putInt(getContext().getString(R.string.saved_money), currMoney - cost);
				editor.apply();
				setText();
			}
		});
	}
}
