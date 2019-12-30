package com.samuelberrien.odyspace.ui.infos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.samuelberrien.odyspace.R;

public class BonusInfosView extends ItemInfosView {

	public BonusInfosView(Activity mActivity) {
		super(mActivity);
	}

	@Override
	protected Item3DView make3DView(Context context, String itemName) {
		return new Bonus3DView(getContext(), itemName);
	}

	@Override
	protected String loadName() {
		return savedShip.getString(
				getContext().getString(R.string.current_bonus_used),
				getContext().getString(R.string.bonus_1));
	}

	@Override
	protected void setText() {
		int currentBoughtDuration = savedShop.getInt(
				getContext().getString(R.string.bought_duration),
				getResources().getInteger(R.integer.zero));
		int currentBonusDuration = savedShip.getInt(
				getContext().getString(R.string.current_bonus_duration),
				getResources().getInteger(R.integer.zero));
		infos.setText(getContext().getString(R.string.bonus_info_drawer, itemName, currentBonusDuration, currentBoughtDuration));
	}

	@Override
	protected void fillItemChooser() {
		titleItemChooser.setText(getContext().getString(R.string.bought_bonus));
		String[] items = getResources().getStringArray(R.array.bonus_shop_list_item);
		final int[] durationList = getResources().getIntArray(
				R.array.bonus_duration_shop_list_item);
		for (int i = 0; i < items.length; i++) {
			int rBool = items[i].equals(getContext().getString(R.string.bonus_1)) ?
					R.bool.vrai : R.bool.faux;
			boolean bool = getResources().getBoolean(rBool);
			if (savedShop.getBoolean(items[i], bool)) {
				RadioButton tmpRadioButton = new RadioButton(getContext());
				tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));

				radioGroup.addView(tmpRadioButton);
				tmpRadioButton.setText(items[i]);

				final int index = i;
				tmpRadioButton.setOnClickListener((view) ->
						savedShip.edit()
								.putString(getContext().getString(R.string.current_bonus_used), items[index])
								.putInt(getContext().getString(R.string.current_bonus_duration), durationList[index])
								.apply()
				);

				if (savedShip.getString(getContext().getString(R.string.current_bonus_used),
						getContext().getString(R.string.bonus_1))
						.equals(items[index])) {
					tmpRadioButton.setChecked(true);
				}
			}
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(getContext().getString(R.string.current_bonus_used)) || key.equals(getContext().getString(R.string.bought_duration)))
			reinit();
	}
}
