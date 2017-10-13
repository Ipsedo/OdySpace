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
		if (key.equals(parentsActivity.getString(R.string.current_fire_type))) {
			if (kind == FIRE) {
				reinit();
			}
		} else if (key.equals(parentsActivity.getString(R.string.current_ship_used))) {
			if (kind == SHIP) {
				reinit();
			}
		} else if (key.equals(parentsActivity.getString(R.string.current_bonus_used))) {
			if (kind == BONUS) {
				reinit();
			}
		} else if (key.equals(parentsActivity.getString(R.string.saved_money))) {
			reloadItemChooser();
		} else if (key.equals(parentsActivity.getString(R.string.bought_life))) {
			if (kind == SHIP) {
				setText();
			}
		} else if (key.equals(parentsActivity.getString(R.string.bought_duration))) {
			if (kind == BONUS) {
				setText();
			}
		}
	}

	private SharedPreferences savedShop;
	private SharedPreferences savedShip;

	private String itemName;

	private LinearLayout.LayoutParams layoutParams;

	private final Item3DWindow item3DWindow;
	private TextView infos;

	private Purchases kind;

	private Activity parentsActivity;

	private LinearLayout selectItemLayout;
	private TextView titleItemChooser;
	private RadioGroup radioGroup;

	public ItemInfosView(Activity mActivity, Purchases kind) {
		super(mActivity);
		setOrientation(LinearLayout.HORIZONTAL);

		parentsActivity = mActivity;
		this.kind = kind;

		savedShop = parentsActivity.getApplicationContext().getSharedPreferences(parentsActivity.getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		savedShip = parentsActivity.getApplicationContext().getSharedPreferences(parentsActivity.getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);

		savedShip.registerOnSharedPreferenceChangeListener(this);
		savedShop.registerOnSharedPreferenceChangeListener(this);

		layoutParams = getLayoutParams(parentsActivity);


		loadName();
		switch (kind) {
			case SHIP:
				item3DWindow = new Item3DWindow(parentsActivity, Purchases.SHIP, itemName);
				break;
			case FIRE:
				item3DWindow = new Item3DWindow(parentsActivity, Purchases.FIRE, itemName);
				break;
			case BONUS:
				item3DWindow = new Item3DWindow(parentsActivity, Purchases.SHIP, itemName);
				break;
			default:
				item3DWindow = new Item3DWindow(parentsActivity, Purchases.SHIP, itemName);
				break;

		}
		makeText();

		addView(item3DWindow, layoutParams);
		addView(infos, layoutParams);

		setBackground(ContextCompat.getDrawable(parentsActivity, R.drawable.drawer_button));

		dialog = new Dialog(parentsActivity, R.style.AppTheme);
		makeItemChooser();
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(selectItemLayout);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Point screenSize = getScreenSize(parentsActivity);
				dialog.getWindow().setLayout(screenSize.x * 3 / 4, screenSize.y / 2);
				selectItemLayout.requestLayout();
				dialog.show();
			}
		});
	}

	private void reinit() {
		loadName();
		setText();
		item3DWindow.changeObj(kind, itemName);
	}

	private void loadName() {
		switch (kind) {
			case SHIP:
				itemName = savedShip.getString(parentsActivity.getString(R.string.current_ship_used), parentsActivity.getString(R.string.saved_ship_used_default));
				break;
			case FIRE:
				itemName = savedShip.getString(parentsActivity.getString(R.string.current_fire_type), parentsActivity.getString(R.string.saved_fire_type_default));
				break;
			case BONUS:
				itemName = savedShip.getString(parentsActivity.getString(R.string.current_bonus_used), parentsActivity.getString(R.string.bonus_1));
				break;
		}
	}

	private void make3DView() {

	}

	private void setText() {
		switch (kind) {
			case SHIP:
				int currBoughtLife = savedShop.getInt(parentsActivity.getString(R.string.bought_life), parentsActivity.getResources().getInteger(R.integer.saved_ship_life_shop_default));
				int currShipLife = savedShip.getInt(parentsActivity.getString(R.string.current_life_number), parentsActivity.getResources().getInteger(R.integer.saved_ship_life_default));
				infos.setText(itemName + System.getProperty("line.separator") + "Life : " + currShipLife + " + " + currBoughtLife);
				break;
			case FIRE:
				infos.setText(itemName);
				break;
			case BONUS:
				break;

		}
	}

	private void makeText() {
		infos = new TextView(parentsActivity);
		infos.setGravity(Gravity.CENTER);
		setText();
	}

	private void fillItemChooser() {
		final String[] items;

		switch (kind) {
			case SHIP:
				titleItemChooser.setText("Bought ships");
				items = parentsActivity.getResources().getStringArray(R.array.ship_shop_list_item);
				final int[] lifeList = parentsActivity.getResources().getIntArray(R.array.ship_life_shop_list_item);
				for (int i = 1; i < items.length; i++) {
					int rBool = items[i].equals(parentsActivity.getString(R.string.ship_simple)) ? R.bool.vrai : R.bool.faux;
					if (savedShop.getBoolean(items[i], parentsActivity.getResources().getBoolean(rBool))) {
						RadioButton tmpRadioButton = new RadioButton(parentsActivity);
						tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

						radioGroup.addView(tmpRadioButton);
						tmpRadioButton.setText(items[i]);

						final int index = i;
						tmpRadioButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								savedShip.edit()
										.putString(parentsActivity.getString(R.string.current_ship_used), items[index])
										.putInt(parentsActivity.getString(R.string.current_life_number), lifeList[index - 1])
										.apply();
							}
						});

						if (savedShip.getString(parentsActivity.getString(R.string.current_ship_used), parentsActivity.getString(R.string.saved_ship_used_default)).equals(items[index])) {
							tmpRadioButton.setChecked(true);
						}
					}
				}
				break;
			case FIRE:
				titleItemChooser.setText("Bought fires");
				items = parentsActivity.getResources().getStringArray(R.array.fire_shop_list_item);
				for (int i = 0; i < items.length; i++) {
					int rBool = items[i].equals(parentsActivity.getString(R.string.fire_1)) ? R.bool.vrai : R.bool.faux;
					if (savedShop.getBoolean(items[i], parentsActivity.getResources().getBoolean(rBool))) {
						RadioButton tmpRadioButton = new RadioButton(parentsActivity);
						tmpRadioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

						radioGroup.addView(tmpRadioButton);
						tmpRadioButton.setText(items[i]);

						final int index = i;
						tmpRadioButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								savedShip.edit()
										.putString(parentsActivity.getString(R.string.current_fire_type), items[index])
										.apply();
							}
						});

						if (savedShip.getString(parentsActivity.getString(R.string.current_fire_type), parentsActivity.getString(R.string.saved_fire_type_default)).equals(items[index])) {
							tmpRadioButton.setChecked(true);
						}
					}
				}
				break;
			case BONUS:
				break;

		}
	}

	private void makeItemChooser() {
		selectItemLayout = (LinearLayout) parentsActivity.getLayoutInflater().inflate(R.layout.select_item_layout, (LinearLayout) parentsActivity.findViewById(R.id.select_item_layout));
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

	private Point getScreenSize(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	private LinearLayout.LayoutParams getLayoutParams(Context context) {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		layoutParams.setMargins(5, 5, 5, 5 + (int) px);
		return layoutParams;
	}
}
