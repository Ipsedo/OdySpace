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
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;

/**
 * Created by samuel on 12/10/17.
 */

public class ItemInfosBuilder {

	public static Dialog dialog;

	public static View makeFireInfos(final Activity activity, final String currFireType) {
		final SharedPreferences savedShop = activity.getApplicationContext().getSharedPreferences(activity.getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		final SharedPreferences savedShip = activity.getApplicationContext().getSharedPreferences(activity.getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);

		LinearLayout linearLayout = new LinearLayout(activity);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams layoutParams = getLayoutParams(activity);

		linearLayout.addView(Item3DWindow.makeFireView(activity, currFireType), layoutParams);

		TextView fireName = new TextView(activity);
		fireName.setText(currFireType);
		fireName.setGravity(Gravity.CENTER);

		final LinearLayout selectItemLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.select_item_layout, (LinearLayout) activity.findViewById(R.id.select_item_layout));
		RadioGroup radioGroup = (RadioGroup) selectItemLayout.findViewById(R.id.select_item_radio_group);
		TextView textView = (TextView) selectItemLayout.findViewById(R.id.select_item_text);

		textView.setText("Bought fires");
		final String[] items = activity.getResources().getStringArray(R.array.fire_shop_list_item);
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

		linearLayout.addView(fireName, layoutParams);

		linearLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.drawer_button));

		linearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ItemInfosBuilder.showDialog(activity, selectItemLayout);
			}
		});

		return linearLayout;
	}

	public static View makeShipInfos(final Activity activity, final String shipUsed) {

		final SharedPreferences savedShop = activity.getApplicationContext().getSharedPreferences(activity.getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		final SharedPreferences savedShip = activity.getApplicationContext().getSharedPreferences(activity.getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);

		LinearLayout linearLayout = new LinearLayout(activity);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams layoutParams = getLayoutParams(activity);

		linearLayout.addView(Item3DWindow.makeShipView(activity, shipUsed), layoutParams);

		int currBoughtLife = savedShop.getInt(activity.getString(R.string.bought_life), activity.getResources().getInteger(R.integer.saved_ship_life_shop_default));
		int currShipLife = savedShip.getInt(activity.getString(R.string.current_life_number), activity.getResources().getInteger(R.integer.saved_ship_life_default));
		TextView fireName = new TextView(activity);
		fireName.setText(shipUsed + System.getProperty("line.separator") + "Life : " + currShipLife + " + " + currBoughtLife);
		fireName.setGravity(Gravity.CENTER);

		final LinearLayout selectItemLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.select_item_layout, (LinearLayout) activity.findViewById(R.id.select_item_layout));
		RadioGroup radioGroup = (RadioGroup) selectItemLayout.findViewById(R.id.select_item_radio_group);
		TextView textView = (TextView) selectItemLayout.findViewById(R.id.select_item_text);

		textView.setText("Bought ships");
		final String[] items = activity.getResources().getStringArray(R.array.ship_shop_list_item);
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

		linearLayout.addView(fireName, layoutParams);

		linearLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.drawer_button));

		linearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ItemInfosBuilder.showDialog(activity, selectItemLayout);
			}
		});

		return linearLayout;
	}

	private static Point getScreenSize(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	private static void showDialog(Activity activity, LinearLayout selectItemLayout) {
		ViewGroup parent = (ViewGroup) selectItemLayout.getParent();
		if (parent != null)
			parent.removeView(selectItemLayout);

		dialog = new Dialog(activity, R.style.AppTheme);

		Point screenSize = getScreenSize(activity);
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(screenSize.x * 3 / 4, screenSize.y / 2);
		selectItemLayout.setLayoutParams(layoutParams2);

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(selectItemLayout);

		dialog.setCancelable(true);

		dialog.getWindow().setLayout(screenSize.x * 3 / 4, screenSize.y / 2);

		dialog.show();
	}

	private static LinearLayout.LayoutParams getLayoutParams(Context context) {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		layoutParams.setMargins(5, 5, 5, 5 + (int) px);
		return layoutParams;
	}
}
