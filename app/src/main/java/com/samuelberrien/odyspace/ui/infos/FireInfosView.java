package com.samuelberrien.odyspace.ui.infos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.samuelberrien.odyspace.R;

public class FireInfosView extends ItemInfosView {

	public FireInfosView(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected Item3DView make3DView(Context context, String itemName) {
		return new Fire3DView(getContext(), itemName);
	}

	@Override
	protected String loadName() {
		return savedShip.getString(
				getContext().getString(R.string.current_fire_type),
				getContext().getString(R.string.saved_fire_type_default));
	}

	@Override
	protected void setText() {
		infos.setText(itemName);
	}

	@Override
	protected void fillItemChooser() {
		titleItemChooser.setText(getContext().getString(R.string.bought_fires));
		String[] items = getResources().getStringArray(R.array.fire_shop_list_item);
		for (int i = 0; i < items.length; i++) {
			int rBool = items[i].equals(getContext().getString(R.string.fire_1)) ?
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
								.putString(
										getContext().getString(R.string.current_fire_type),
										items[index])
								.apply()
				);

				if (savedShip.getString(getContext().getString(R.string.current_fire_type),
						getContext().getString(R.string.saved_fire_type_default))
						.equals(items[index])) {
					tmpRadioButton.setChecked(true);
				}
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		super.onSharedPreferenceChanged(sharedPreferences, key);
		if (key.equals(getContext().getString(R.string.current_fire_type)))
			reinit();
	}
}
