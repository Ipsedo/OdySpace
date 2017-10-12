package com.samuelberrien.odyspace.utils.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.text.Text;

import java.util.ArrayList;

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

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		layoutParams.setMargins(0, 5, 0, 0);

		if (currFireType.equals(activity.getString(R.string.fire_1))) {
			linearLayout.addView(new Item3DWindow(activity, "rocket.obj", "rocket.mtl"), layoutParams);
			//imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.simple_fire));
		} else if (currFireType.equals(activity.getString(R.string.fire_2))) {
			linearLayout.addView(new Item3DWindow(activity, "rocket.obj", "rocket.mtl"), layoutParams);
			//imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.quint_fire));
		} else if (currFireType.equals(activity.getString(R.string.fire_3))) {
			linearLayout.addView(new Item3DWindow(activity, "bomb.obj", "bomb.mtl"), layoutParams);
			//imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.simple_bomb));
		} else if (currFireType.equals(activity.getString(R.string.fire_4))) {
			linearLayout.addView(new Item3DWindow(activity, "rocket.obj", "rocket.mtl"), layoutParams);
			//imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.triple_fire));
		} else if (currFireType.equals(activity.getString(R.string.fire_5))) {
			linearLayout.addView(new Item3DWindow(activity, "laser.obj", "laser.mtl"), layoutParams);
			//imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.laser));
		} else if (currFireType.equals(activity.getString(R.string.fire_6))) {
			linearLayout.addView(new Item3DWindow(activity, "torus.obj", "torus.mtl"), layoutParams);
			//imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.torus));
		}
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

		LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams1.weight = 0.3f;


		dialog = new Dialog(activity, R.style.AppTheme);
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
		selectItemLayout.addView(okDialog);

		linearLayout.addView(fireName, layoutParams);

		linearLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.drawer_button));

		linearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Point screenSize = getScreenSize(activity);
				LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(screenSize.x * 3 / 4, screenSize.y / 2);
				selectItemLayout.setLayoutParams(layoutParams2);

				dialog.setContentView(selectItemLayout);

				dialog.getWindow().setLayout(screenSize.x * 3 / 4, screenSize.y / 2);
				dialog.show();
				selectItemLayout.requestLayout();
			}
		});

		return linearLayout;
	}

	public static View makeShipInfos(final Activity activity, final String shipUsed) {

		final SharedPreferences savedShop = activity.getApplicationContext().getSharedPreferences(activity.getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		final SharedPreferences savedShip = activity.getApplicationContext().getSharedPreferences(activity.getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);

		LinearLayout linearLayout = new LinearLayout(activity);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		layoutParams.setMargins(0, 5, 0, 5);

		if (shipUsed.equals(activity.getString(R.string.ship_simple))) {
			linearLayout.addView(new Item3DWindow(activity, "ship_3.obj", "ship_3.mtl"), layoutParams);
		} else if (shipUsed.equals(activity.getString(R.string.ship_bird))) {
			linearLayout.addView(new Item3DWindow(activity, "ship_bird.obj", "ship_bird.mtl"), layoutParams);
		} else if (shipUsed.equals(activity.getString(R.string.ship_supreme))) {
			linearLayout.addView(new Item3DWindow(activity, "ship_supreme.obj", "ship_supreme.mtl"), layoutParams);
		}

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

		LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams1.weight = 0.3f;

		dialog = new Dialog(activity, R.style.AppTheme);
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
		selectItemLayout.addView(okDialog);

		linearLayout.addView(fireName, layoutParams);

		linearLayout.setBackground(ContextCompat.getDrawable(activity, R.drawable.drawer_button));

		linearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Point screenSize = getScreenSize(activity);

				LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(screenSize.x * 3 / 4, screenSize.y / 2);
				selectItemLayout.setLayoutParams(layoutParams2);

				dialog.setContentView(selectItemLayout);

				dialog.getWindow().setLayout(screenSize.x * 3 / 4, screenSize.y / 2);
				dialog.show();
				selectItemLayout.requestLayout();
			}
		});

		return linearLayout;
	}

	private static Point getScreenSize(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return  size;
	}
}
