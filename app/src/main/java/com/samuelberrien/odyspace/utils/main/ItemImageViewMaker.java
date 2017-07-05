package com.samuelberrien.odyspace.utils.main;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.samuelberrien.odyspace.R;

/**
 * Created by samuel on 05/07/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public final class ItemImageViewMaker {

	public static void makeFireTypeImage(final Activity activity, final ImageView imageView, final String currFireType) {
		if (currFireType.equals(activity.getString(R.string.fire_bonus_1))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.simple_fire));
		} else if (currFireType.equals(activity.getString(R.string.fire_bonus_2))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.quint_fire));
		} else if (currFireType.equals(activity.getString(R.string.fire_bonus_3))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.simple_bomb));
		} else if (currFireType.equals(activity.getString(R.string.fire_bonus_4))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.triple_fire));
		}
		imageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				ViewHelper.makeViewTransition(activity, imageView);
				Toast.makeText(activity, currFireType, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
	}

	public static void makeShipImage(final Activity activity, final ImageView imageView, final String shipUsed, final int currShipLife, final int currBoughtLife) {
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
				Toast.makeText(activity, shipUsed + System.getProperty("line.separator") + "Life : " + currShipLife + " + " + currBoughtLife, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
	}
}
