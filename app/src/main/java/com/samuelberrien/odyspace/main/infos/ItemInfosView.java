package com.samuelberrien.odyspace.main.infos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.game.Purchases;

import static com.samuelberrien.odyspace.utils.game.Purchases.BONUS;
import static com.samuelberrien.odyspace.utils.game.Purchases.FIRE;
import static com.samuelberrien.odyspace.utils.game.Purchases.SHIP;

/**
 * Created by samuel on 12/10/17.
 */

public class ItemInfosView extends LinearLayout implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Dialog dialog;

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(getContext().getString(R.string.current_fire_type))) {
			if (kind == FIRE) {
				reinit();
			}
		} else if (key.equals(getContext().getString(R.string.current_ship_used))) {
			if (kind == SHIP) {
				reinit();
			}
		} else if (key.equals(getContext().getString(R.string.current_bonus_used))) {
			if (kind == BONUS) {
				reinit();
			}
		} else if (key.equals(getContext().getString(R.string.saved_money))) {
			reloadItemChooser();
		} else if (key.equals(getContext().getString(R.string.bought_life))) {
			if (kind == SHIP) {
				setText();
			}
		} else if (key.equals(getContext().getString(R.string.bought_duration))) {
			if (kind == BONUS) {
				setText();
			}
		}
	}

	private SharedPreferences savedShop;
	private SharedPreferences savedShip;

	private String itemName;

	private LinearLayout.LayoutParams layoutParams;

	private final Item3DView item3DView;
	private TextView infos;

	private Purchases kind;

	//private Activity parentsActivity;
	private LayoutInflater layoutInflater;

	private LinearLayout selectItemLayout;
	private TextView titleItemChooser;
	private RadioGroup radioGroup;

	public ItemInfosView(Activity mActivity, Purchases kind) {
		super(mActivity);
		setOrientation(LinearLayout.HORIZONTAL);

		layoutInflater = mActivity.getLayoutInflater();
		this.kind = kind;

		savedShop = mActivity.getApplicationContext()
				.getSharedPreferences(getContext().getString(R.string.shop_preferences),
						Context.MODE_PRIVATE);
		savedShip = mActivity.getApplicationContext()
				.getSharedPreferences(getContext().getString(R.string.ship_info_preferences),
						Context.MODE_PRIVATE);

		savedShip.registerOnSharedPreferenceChangeListener(this);
		savedShop.registerOnSharedPreferenceChangeListener(this);

		layoutParams = getLayoutParams(getContext());


		loadName();
		switch (kind) {
			case SHIP:
				item3DView = new Item3DView(getContext(), Purchases.SHIP, itemName);
				break;
			case FIRE:
				item3DView = new Item3DView(getContext(), Purchases.FIRE, itemName);
				break;
			case BONUS:
				item3DView = new Item3DView(getContext(), Purchases.BONUS, itemName);
				break;
			default:
				item3DView = new Item3DView(getContext(), Purchases.SHIP, itemName);
				break;
		}
		makeText();

		addView(item3DView, layoutParams);
		addView(infos, layoutParams);

		setBackground(ContextCompat.getDrawable(getContext(), R.drawable.drawer_button));

		dialog = new Dialog(getContext(), R.style.AppTheme);
		makeItemChooser();
		dialog.getWindow()
				.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(selectItemLayout);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Point screenSize = getScreenSize();
				dialog.getWindow().setLayout(screenSize.x * 3 / 4, screenSize.y / 2);
				selectItemLayout.requestLayout();
				dialog.show();
			}
		});
	}

	private void reinit() {
		loadName();
		setText();
		item3DView.changeObj(kind, itemName);
	}

	private void loadName() {
		switch (kind) {
			case SHIP:
				itemName = savedShip.getString(
						getContext().getString(R.string.current_ship_used),
						getContext().getString(R.string.saved_ship_used_default));
				break;
			case FIRE:
				itemName = savedShip.getString(
						getContext().getString(R.string.current_fire_type),
						getContext().getString(R.string.saved_fire_type_default));
				break;
			case BONUS:
				itemName = savedShip.getString(
						getContext().getString(R.string.current_bonus_used),
						getContext().getString(R.string.bonus_1));
				break;
		}
	}

	private void make3DView() {

	}

	private void setText() {
		switch (kind) {
			case SHIP:
				int currBoughtLife = savedShop.getInt(
						getContext().getString(R.string.bought_life),
						getResources().getInteger(R.integer.saved_ship_life_shop_default));
				int currShipLife = savedShip.getInt(
						getContext().getString(R.string.current_life_number),
						getResources().getInteger(R.integer.saved_ship_life_default));
				infos.setText(itemName + System.getProperty("line.separator")
						+ "Life : " + currShipLife + " + " + currBoughtLife);
				break;
			case FIRE:
				infos.setText(itemName);
				break;
			case BONUS:
				int currentBoughtDuration = savedShop.getInt(
						getContext().getString(R.string.bought_duration),
						getResources().getInteger(R.integer.zero));
				int currentBonusDuration = savedShip.getInt(
						getContext().getString(R.string.current_bonus_duration),
						getResources().getInteger(R.integer.zero));
				infos.setText(itemName + System.getProperty("line.separator")
						+ "Time : " + currentBonusDuration + " + " + currentBoughtDuration);
				break;

		}
	}

	private void makeText() {
		infos = new TextView(getContext());
		infos.setGravity(Gravity.CENTER);
		setText();
	}

	private void fillItemChooser() {
		final String[] items;

		switch (kind) {
			case SHIP:
				titleItemChooser.setText("Bought ships");
				items = getResources().getStringArray(R.array.ship_shop_list_item);
				final int[] lifeList = getResources().getIntArray(R.array.ship_life_shop_list_item);
				for (int i = 1; i < items.length; i++) {
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
						tmpRadioButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								savedShip.edit()
										.putString(
												getContext().getString(R.string.current_ship_used),
												items[index])
										.putInt(
												getContext().getString(R.string.current_life_number),
												lifeList[index - 1])
										.apply();
							}
						});

						if (savedShip.getString(getContext().getString(R.string.current_ship_used),
								getContext().getString(R.string.saved_ship_used_default))
								.equals(items[index])) {
							tmpRadioButton.setChecked(true);
						}
					}
				}
				break;
			case FIRE:
				titleItemChooser.setText("Bought fires");
				items = getResources().getStringArray(R.array.fire_shop_list_item);
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
						tmpRadioButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								savedShip.edit()
										.putString(
												getContext().getString(R.string.current_fire_type),
												items[index])
										.apply();
							}
						});

						if (savedShip.getString(getContext().getString(R.string.current_fire_type),
								getContext().getString(R.string.saved_fire_type_default))
								.equals(items[index])) {
							tmpRadioButton.setChecked(true);
						}
					}
				}
				break;
			case BONUS:
				titleItemChooser.setText("Bought bonus");
				items = getResources().getStringArray(R.array.bonus_shop_list_item);
				final int[] durationList = getResources().getIntArray(
						R.array.bonus_duration_shop_list_item);
				for (int i = 1; i < items.length; i++) {
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
						tmpRadioButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								savedShip.edit()
										.putString(getContext().getString(R.string.current_bonus_used),
												items[index])
										.putInt(getContext().getString(R.string.current_bonus_duration),
												durationList[index - 1])
										.apply();
							}
						});

						if (savedShip.getString(getContext().getString(R.string.current_bonus_used),
								getContext().getString(R.string.bonus_1))
								.equals(items[index])) {
							tmpRadioButton.setChecked(true);
						}
					}
				}
				break;

		}
	}

	private void makeItemChooser() {
		selectItemLayout = (LinearLayout) layoutInflater
				.inflate(R.layout.select_item_layout,
						(LinearLayout) findViewById(R.id.select_item_layout));
		radioGroup = (RadioGroup) selectItemLayout.findViewById(R.id.select_item_radio_group);
		titleItemChooser = (TextView) selectItemLayout.findViewById(R.id.select_item_text);
		fillItemChooser();
	}

	private void reloadItemChooser() {
		radioGroup.removeAllViews();
		fillItemChooser();
	}


	/*LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams1.weight = 0.3f;

		TextView okDialog = new TextView(activity);
		okDialog.setText("Ok");
		okDialog.setGravity(Gravity.CENTER);
		okDialog.setLayoutParams(layoutParams1);
		okDialog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		selectItemLayout.addView(okDialog);*/

	public void dismissDialog() {
		dialog.dismiss();
	}

	private Point getScreenSize() {
		Display display = getDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	private LinearLayout.LayoutParams getLayoutParams(Context context) {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		layoutParams.setMargins(5, 5, 5, 5 + (int) px);
		return layoutParams;
	}
}
