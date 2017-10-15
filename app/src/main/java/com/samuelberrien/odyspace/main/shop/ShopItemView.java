package com.samuelberrien.odyspace.main.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.main.infos.Item3DView;
import com.samuelberrien.odyspace.utils.game.Purchases;

/**
 * Created by samuel on 13/10/17.
 */

public class ShopItemView extends LinearLayout {

	private Item3DView item3DView;

	private TextView infos;

	private Purchases kind;
	private int index;

	private SharedPreferences savedShop;

	private Button buyButton;

	public ShopItemView(Context context, Purchases kind, int indexItem) {
		super(context);
		setOrientation(HORIZONTAL);

		this.kind = kind;
		index = indexItem;
		savedShop = context.getSharedPreferences(context.getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		infos = new TextView(context);
		infos.setGravity(Gravity.CENTER);
		buyButton = new Button(context);
		buyButton.setText("Buy");
		buyButton.setBackground(ContextCompat.getDrawable(context, R.drawable.drawer_button));

		//item3DWindow = new Item3DWindow(context, Purchases.SHIP, context.getString(R.string.ship_supreme));
		makeItem3D();

		setText();

		updateButton();

		LayoutParams layoutParams = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				1, r.getDisplayMetrics());
		layoutParams.setMargins(5, 5, 5, 5 + (int) px);

		addView(item3DView, layoutParams);
		addView(infos, layoutParams);

		layoutParams = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;
		addView(buyButton, layoutParams);

		setBackground(ContextCompat.getDrawable(context, R.drawable.button_unpressed));
	}

	private void makeItem3D() {
		String[] names;
		switch (kind) {
			case SHIP:
				names = getResources().getStringArray(R.array.ship_shop_list_item);
				item3DView = new Item3DView(getContext(), Purchases.SHIP, names[index]);
				break;
			case FIRE:
				names = getResources().getStringArray(R.array.fire_shop_list_item);
				item3DView = new Item3DView(getContext(), Purchases.FIRE, names[index]);
				break;
			case BONUS:
				names = getResources().getStringArray(R.array.bonus_shop_list_item);
				item3DView = new Item3DView(getContext(), Purchases.BONUS, names[index]);
				break;
		}
	}

	private void setText() {
		String[] items;
		int[] price;
		int cost;
		switch (kind) {
			case SHIP:
				items = getResources().getStringArray(R.array.ship_shop_list_item);
				price = getResources().getIntArray(R.array.ship_shop_price);
				String life = "";
				if (index == 0) {
					int currentBoughtLife = savedShop.getInt(
							getContext().getString(R.string.bought_life),
							getResources().getInteger(R.integer.zero));
					cost = (int) Math.pow(currentBoughtLife, 2d) * price[index];
				} else {
					int[] lifes = getResources().getIntArray(R.array.ship_life_shop_list_item);
					life += "life : " + lifes[index - 1];
					cost = price[index];
				}
				infos.setText(items[index] + System.getProperty("line.separator")
						+ life + System.getProperty("line.separator") + "cost : " + cost);
				break;
			case FIRE:
				items = getResources().getStringArray(R.array.fire_shop_list_item);
				price = getResources().getIntArray(R.array.fire_shop_price);
				infos.setText(items[index] + System.getProperty("line.separator")
						+ System.getProperty("line.separator") + "cost : " + price[index]);
				break;
			case BONUS:
				items = getResources().getStringArray(R.array.bonus_shop_list_item);
				price = getResources().getIntArray(R.array.bonus_shop_price);

				String duration = "";
				if (index == 0) {
					int currentBoughtDuration = savedShop.getInt(
							getContext().getString(R.string.bought_duration),
							getResources().getInteger(R.integer.zero));
					cost = (int) Math.pow(currentBoughtDuration / 10, 2d) * price[index];
				} else {
					int[] durations = getResources().getIntArray(R.array.bonus_duration_shop_list_item);
					duration += "duration : " + durations[index - 1];
					cost = price[index];
				}
				infos.setText(items[index] + System.getProperty("line.separator")
						+ duration + System.getProperty("line.separator") + "cost : " + cost);
				break;
		}
	}


	private void updateButton() {
		final int[] price;
		final String[] items;
		final SharedPreferences.Editor editor = savedShop.edit();
		int rBool;
		switch (kind) {
			case SHIP:
				items = getResources().getStringArray(R.array.ship_shop_list_item);
				price = getResources().getIntArray(R.array.ship_shop_price);
				if (index == 0) {
					buyButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							int currMoney = savedShop.getInt(
									getContext().getString(R.string.saved_money),
									getResources().getInteger(R.integer.saved_init_money));
							int currentBoughtLife = savedShop.getInt(
									getContext().getString(R.string.bought_life),
									getResources().getInteger(R.integer.zero));
							int lifeCost = (int) Math.pow(currentBoughtLife, 2d) * price[index];
							if (currMoney >= lifeCost) {
								editor.putInt(
										getContext().getString(R.string.bought_life),
										currentBoughtLife + 1);
								editor.putInt(
										getContext().getString(R.string.saved_money),
										currMoney - lifeCost);
								editor.apply();
								setText();
							}
						}
					});
				} else {
					buyButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
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
								//insertPrice(cost[index]);
							}
						}
					});
					rBool = items[index].equals(getContext().getString(R.string.ship_simple)) ?
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
				break;
			case FIRE:
				items = getResources().getStringArray(R.array.fire_shop_list_item);
				price = getResources().getIntArray(R.array.fire_shop_price);
				buyButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
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
							//insertPrice(cost[index]);
						}
					}
				});
				rBool = items[index].equals(getContext().getString(R.string.fire_1)) ?
						R.bool.vrai : R.bool.faux;
				if (savedShop.getBoolean(items[index], getResources().getBoolean(rBool))) {
					buyButton.setClickable(false);
					buyButton.setBackground(
							ContextCompat.getDrawable(getContext(),
									R.drawable.button_pressed));
				} else {
					buyButton.setClickable(true);
				}
				break;
			case BONUS:
				items = getResources().getStringArray(R.array.bonus_shop_list_item);
				price = getResources().getIntArray(R.array.bonus_shop_price);
				if (index == 0) {
					buyButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							int currentBoughtDuration = savedShop.getInt(
									getContext().getString(R.string.bought_duration),
									getResources().getInteger(R.integer.zero));
							int currentPrice = (int) Math.pow(currentBoughtDuration / 10, 2d) * price[index];
							int currMoney = savedShop.getInt(
									getContext().getString(R.string.saved_money),
									getResources().getInteger(R.integer.saved_init_money));
							if (currMoney >= currentPrice) {
								editor.putInt(
										getContext().getString(R.string.bought_duration),
										currentBoughtDuration + 10);
								editor.putInt(
										getContext().getString(R.string.saved_money),
										currMoney - currentPrice);
								editor.apply();
								setText();
							}
						}
					});
				} else {
					buyButton.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
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
								//insertPrice(cost[index]);
							}
						}
					});
					rBool = items[index].equals(getContext().getString(R.string.bonus_1)) ?
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
				break;
		}
	}
}
