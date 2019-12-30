package com.samuelberrien.odyspace.ui.infos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.samuelberrien.odyspace.R;

public class ShipInfosView extends ItemInfosView {

	public ShipInfosView(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected Item3DView make3DView(Context context, String itemName) {
		return new Ship3DView(getContext(), itemName);
	}

	@Override
	protected String loadName() {
		return savedShip.getString(
				getContext().getString(R.string.current_ship_used),
				getContext().getString(R.string.saved_ship_used_default));
	}

	@Override
	protected void setText() {
		int currBoughtLife = savedShop.getInt(getContext().getString(R.string.bought_life), getResources().getInteger(R.integer.saved_ship_life_shop_default));
		int currShipLife = savedShip.getInt(getContext().getString(R.string.current_life_number), getResources().getInteger(R.integer.saved_ship_life_default));
		infos.setText(getContext().getString(R.string.ship_info_drawer, itemName, currShipLife, currBoughtLife));
	}

	@Override
	protected void fillItemChooser() {
		titleItemChooser.setText(getContext().getString(R.string.bought_ships));
		String[] items = getResources().getStringArray(R.array.ship_shop_list_item);
		final int[] lifeList = getResources().getIntArray(R.array.ship_life_shop_list_item);

		for (int i = 0; i < items.length; i++) {

			int rBool = items[i].equals(getContext().getString(R.string.ship_simple)) ?
					R.bool.vrai : R.bool.faux;

			if (savedShop.getBoolean(items[i], getResources().getBoolean(rBool))) {
				RadioButton tmpRadioButton = new RadioButton(getContext());
				tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));

				radioGroup.addView(tmpRadioButton);
				tmpRadioButton.setText(items[i]);

				final int index = i;
				tmpRadioButton.setOnClickListener((view) ->
						savedShip.edit()
								.putString(getContext().getString(R.string.current_ship_used), items[index])
								.putInt(getContext().getString(R.string.current_life_number), lifeList[index])
								.apply()

				);

				if (savedShip.getString(getContext().getString(R.string.current_ship_used),
						getContext().getString(R.string.saved_ship_used_default))
						.equals(items[index])) {
					tmpRadioButton.setChecked(true);
				}
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		super.onSharedPreferenceChanged(sharedPreferences, key);
		if (key.equals(getContext().getString(R.string.current_ship_used)) || key.equals(getContext().getString(R.string.bought_life)))
			reinit();
	}
}
