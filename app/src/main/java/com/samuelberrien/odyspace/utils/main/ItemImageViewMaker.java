package com.samuelberrien.odyspace.utils.main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.game.Purchases;

/**
 * Created by samuel on 05/07/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public final class ItemImageViewMaker {

	public static void makeFireTypeImage(final Activity activity, final Toast myToast, final ImageView imageView, final String currFireType) {
		if (currFireType.equals(activity.getString(R.string.fire_1))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.simple_fire));
		} else if (currFireType.equals(activity.getString(R.string.fire_2))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.quint_fire));
		} else if (currFireType.equals(activity.getString(R.string.fire_3))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.simple_bomb));
		} else if (currFireType.equals(activity.getString(R.string.fire_4))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.triple_fire));
		} else if (currFireType.equals(activity.getString(R.string.fire_5))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.laser));
		} else if (currFireType.equals(activity.getString(R.string.fire_6))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.torus));
		}
		imageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				ViewHelper.makeViewTransition(activity, imageView);
				myToast.setText(currFireType);
				myToast.show();
				return true;
			}
		});
	}

	public static void makeShipImage(final Activity activity, final Toast myToast, final ImageView imageView, final String shipUsed, final int currShipLife, final int currBoughtLife) {
		if (shipUsed.equals(activity.getString(R.string.ship_simple))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.simple_ship));
		} else if (shipUsed.equals(activity.getString(R.string.ship_bird))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ship_bird));
		} else if (shipUsed.equals(activity.getString(R.string.ship_supreme))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ship_supreme));
		}
		imageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				ViewHelper.makeViewTransition(activity, imageView);
				myToast.setText(shipUsed + System.getProperty("line.separator") + "Life : " + currShipLife + " + " + currBoughtLife);
				myToast.show();
				return true;
			}
		});
	}

	public static void makeBonusImage(final Activity activity, final Toast myToast, final ImageView imageView, final String bonusUsed, final int bonusDuration, final int currBoughtDurantion) {
		if (bonusUsed.equals(activity.getString(R.string.bonus_1))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.speed));
		} else if (bonusUsed.equals(activity.getString(R.string.bonus_2))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.shield));
		}

		imageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				ViewHelper.makeViewTransition(activity, imageView);
				myToast.setText(bonusUsed + System.getProperty("line.separator") + "Duration : " + bonusDuration + " + " + currBoughtDurantion);
				myToast.show();
				return true;
			}
		});
	}

	public static View makeSelectItemView(final Activity activity, Purchases type) {
		final SharedPreferences savedShop = activity.getApplicationContext().getSharedPreferences(activity.getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		final SharedPreferences savedShip = activity.getApplicationContext().getSharedPreferences(activity.getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);
		View v = activity.getLayoutInflater().inflate(R.layout.select_item_layout, (LinearLayout) activity.findViewById(R.id.select_item_layout));
		TextView textView = (TextView) v.findViewById(R.id.select_item_text);
		final String[] items;
		RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.select_item_radio_group);
		switch (type) {
			case SHIP:
				textView.setText("Bought ships");
				items = activity.getResources().getStringArray(R.array.ship_shop_list_item);
				final int[] lifeList = activity.getResources().getIntArray(R.array.ship_life_shop_list_item);
				for (int i = 1; i < items.length; i++) {
					int rBool = items[i].equals(activity.getString(R.string.ship_simple)) ? R.bool.vrai : R.bool.faux;
					if (savedShop.getBoolean(items[i], activity.getResources().getBoolean(rBool))) {
						RadioButton tmpRadioButton = new RadioButton(activity);
						tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

						radioGroup.addView(tmpRadioButton);
						tmpRadioButton.setText(items[i]);

						final int index = i;
						tmpRadioButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								savedShip.edit()
										.putString(activity.getString(R.string.current_ship_used), items[index])
										.putInt(activity.getString(R.string.current_life_number), lifeList[index - 1])
										.apply();
							}
						});

						if (savedShip.getString(activity.getString(R.string.current_ship_used), activity.getString(R.string.saved_ship_used_default)).equals(items[index])) {
							tmpRadioButton.setChecked(true);
						}
					}
				}
				break;
			case FIRE:
				textView.setText("Bought fires");
				items = activity.getResources().getStringArray(R.array.fire_shop_list_item);
				for (int i = 0; i < items.length; i++) {
					int rBool = items[i].equals(activity.getString(R.string.fire_1)) ? R.bool.vrai : R.bool.faux;
					if (savedShop.getBoolean(items[i], activity.getResources().getBoolean(rBool))) {
						RadioButton tmpRadioButton = new RadioButton(activity);
						tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

						radioGroup.addView(tmpRadioButton);
						tmpRadioButton.setText(items[i]);

						final int index = i;
						tmpRadioButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								savedShip.edit()
										.putString(activity.getString(R.string.current_fire_type), items[index])
										.apply();
							}
						});

						if (savedShip.getString(activity.getString(R.string.current_fire_type), activity.getString(R.string.saved_fire_type_default)).equals(items[index])) {
							tmpRadioButton.setChecked(true);
						}
					}
				}
				break;
			case BONUS:
				textView.setText("Bought bonus");
				items = activity.getResources().getStringArray(R.array.bonus_shop_list_item);
				final int[] durationList = activity.getResources().getIntArray(R.array.bonus_duration_shop_list_item);
				for (int i = 1; i < items.length; i++) {
					int rBool = items[i].equals(activity.getString(R.string.bonus_1)) ? R.bool.vrai : R.bool.faux;
					boolean bool = activity.getResources().getBoolean(rBool);
					if (savedShop.getBoolean(items[i], bool)) {
						RadioButton tmpRadioButton = new RadioButton(activity);
						tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

						radioGroup.addView(tmpRadioButton);
						tmpRadioButton.setText(items[i]);

						final int index = i;
						tmpRadioButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								savedShip.edit()
										.putString(activity.getString(R.string.current_bonus_used), items[index])
										.putInt(activity.getString(R.string.current_bonus_duration), durationList[index - 1])
										.apply();
							}
						});

						if (savedShip.getString(activity.getString(R.string.current_bonus_used), activity.getString(R.string.bonus_1)).equals(items[index])) {
							tmpRadioButton.setChecked(true);
						}
					}
				}
				break;
		}

		return v;
	}
}
