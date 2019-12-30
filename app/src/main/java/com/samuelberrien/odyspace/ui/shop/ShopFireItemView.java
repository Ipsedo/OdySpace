package com.samuelberrien.odyspace.ui.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.core.Purchases;
import com.samuelberrien.odyspace.ui.infos.Fire3DView;
import com.samuelberrien.odyspace.ui.infos.Item3DView;

public class ShopFireItemView extends ShopItemView {

	public ShopFireItemView(Context context, int indexItem) {
		super(context, indexItem);
	}

	@Override
	protected void makeItem3D() {
		String[] names = getResources().getStringArray(R.array.fire_shop_list_item);
		item3DView = new Fire3DView(getContext(), names[index]);
	}

	@Override
	protected void setText() {
		String[] items = getResources().getStringArray(R.array.fire_shop_list_item);
		int[] prices = getResources().getIntArray(R.array.fire_shop_price);
		infos.setText(getResources().getString(R.string.fire_info_shop, items[index], prices[index]));
	}

	@Override
	protected void updateButton() {
		String[] items = getResources().getStringArray(R.array.fire_shop_list_item);
		int[] price = getResources().getIntArray(R.array.fire_shop_price);
		final SharedPreferences.Editor editor = savedShop.edit();

		buyButton.setOnClickListener((view) -> {
			int currMoney = savedShop.getInt(
					getContext().getString(R.string.saved_money),
					getResources().getInteger(R.integer.saved_init_money));
			if (currMoney >= price[index]) {
				editor.putBoolean(items[index], true);
				editor.putInt(
						getContext().getString(R.string.saved_money),
						currMoney - price[index]);
				editor.apply();
				buyButton.setClickable(false);
				buyButton.setBackground(
						ContextCompat.getDrawable(getContext(),
								R.drawable.button_pressed));
			}

		});

		int rBool = items[index].equals(getContext().getString(R.string.fire_1)) ?
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
