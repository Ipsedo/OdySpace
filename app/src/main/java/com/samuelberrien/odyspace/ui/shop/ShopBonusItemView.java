package com.samuelberrien.odyspace.ui.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.core.Purchases;
import com.samuelberrien.odyspace.ui.infos.Bonus3DView;
import com.samuelberrien.odyspace.ui.infos.Item3DView;

public class ShopBonusItemView extends ShopItemView {

	public ShopBonusItemView(Context context, int indexItem) {
		super(context, indexItem);
	}

	@Override
	protected void makeItem3D() {
		String[] names = getResources().getStringArray(R.array.bonus_shop_list_item);
		item3DView = new Bonus3DView(getContext(), names[index]);
	}

	@Override
	protected void setText() {
		String[] items = getResources().getStringArray(R.array.bonus_shop_list_item);
		int[] prices = getResources().getIntArray(R.array.bonus_shop_price);

		int duration;
		int[] durations = getResources().getIntArray(R.array.bonus_duration_shop_list_item);
		duration = durations[index];
		int cost = prices[index];

		infos.setText(getResources().getString(R.string.bonus_info_shop, items[index], duration, cost));
	}

	@Override
	protected void updateButton() {
		final SharedPreferences.Editor editor = savedShop.edit();
		int[] prices = getResources().getIntArray(R.array.bonus_shop_price);
		String[] items = getResources().getStringArray(R.array.bonus_shop_list_item);

		buyButton.setOnClickListener((v) -> {
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
		int rBool = items[index].equals(getContext().getString(R.string.bonus_1)) ?
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
