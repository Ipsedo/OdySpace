package com.samuelberrien.odyspace.main.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;

import java.util.ArrayList;

/**
 * Created by samuel on 12/10/17.
 */

public class ShopFragment extends Fragment {

	private SharedPreferences savedShop;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		savedShop = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shop_preferences), Context.MODE_PRIVATE);

		View view = inflater.inflate(R.layout.new_shop, container, false);
		ViewPager viewPager = (ViewPager) view.findViewById(R.id.shop_view_pager);
		viewPager.setAdapter(new ShopFragmentPagerAdapter(getChildFragmentManager()));

		TabLayout tabLayout = (TabLayout) view.findViewById(R.id.shop_tab_layout);
		tabLayout.setupWithViewPager(viewPager);
		return view;
	}

	public View setPageChosen(int page, LayoutInflater inflater, ViewGroup container) {

		LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.purchase_list, container, false).findViewById(R.id.layout_list_purchase);
		LinearLayout subLayout = (LinearLayout) linearLayout.findViewById(R.id.layout_list_purchase_scroll);

		final ArrayList<Button> buttonArrayList = new ArrayList<>();

		final SharedPreferences.Editor editor = savedShop.edit();

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
		layoutParams.setMargins(margin, margin, margin, 0);

		if (ShopFragmentPagerAdapter.TAB_TITLES[page].equals(ShopFragmentPagerAdapter.FIRE_TAB)) {
			final String[] fires = getResources().getStringArray(R.array.fire_shop_list_item);
			final int[] cost = getResources().getIntArray(R.array.fire_shop_price);
			for (int i = 0; i < fires.length; i++) {
				final LinearLayout buttons = (LinearLayout) inflater.inflate(R.layout.expand_button, null);
				final TextView fireName = (TextView) buttons.findViewById(R.id.expand_text);
				fireName.setText(fires[i]);
				final Button buy = (Button) buttons.findViewById(R.id.expand_button);
				final int index = i;
				buy.setText("$");
				buy.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int currMoney = savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));
						if (currMoney >= cost[index]) {
							editor.putBoolean(fires[index], true);
							editor.putInt(getString(R.string.saved_money), currMoney - cost[index]);
							editor.apply();
							buy.setClickable(false);
							buy.setBackground(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.drawable_already_buy_button));
							//insertPrice(cost[index]);
						}
					}
				});
				buttonArrayList.add(buy);
				fireName.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for (Button b : buttonArrayList)
							b.setVisibility(View.GONE);
						//insertPrice(cost[index]);
						buy.setVisibility(View.VISIBLE);
					}
				});

				int rBool = fires[i].equals(getString(R.string.fire_1)) ? R.bool.vrai : R.bool.faux;
				if (savedShop.getBoolean(fires[i], getResources().getBoolean(rBool))) {
					buy.setClickable(false);
					buy.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.drawable_already_buy_button));
				} else {
					buy.setClickable(true);
				}
				subLayout.addView(buttons);
				buttons.setLayoutParams(layoutParams);
			}
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[page].equals(ShopFragmentPagerAdapter.SHIP_TAB)) {
			final String[] shipsItem = getResources().getStringArray(R.array.ship_shop_list_item);
			final int[] cost = getResources().getIntArray(R.array.ship_shop_price);
			for (int i = 0; i < shipsItem.length; i++) {
				final LinearLayout buttons = (LinearLayout) inflater.inflate(R.layout.expand_button, null);
				final TextView shipItemName = (TextView) buttons.findViewById(R.id.expand_text);
				shipItemName.setText(shipsItem[i]);
				final Button buy = (Button) buttons.findViewById(R.id.expand_button);
				buy.setText("$");
				final int index = i;
				if (index == 0) {
					buy.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							int currMoney = savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));
							int currentBoughtLife = savedShop.getInt(getString(R.string.bought_life), getResources().getInteger(R.integer.zero));
							int lifeCost = (int) Math.pow(currentBoughtLife, 2d) * cost[index];
							if (currMoney >= lifeCost) {
								editor.putInt(getString(R.string.bought_life), currentBoughtLife + 1);
								editor.putInt(getString(R.string.saved_money), currMoney - lifeCost);
								editor.apply();
								lifeCost = (int) Math.pow(savedShop.getInt(getString(R.string.bought_life), getResources().getInteger(R.integer.zero)), 2d) * cost[index];
								/*insertPrice(lifeCost);
								updateShipInfo();*/
							}
						}
					});
					buttonArrayList.add(buy);
					shipItemName.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for (Button b : buttonArrayList)
								b.setVisibility(View.GONE);
							int currentBoughtLife = savedShop.getInt(getString(R.string.bought_life), getResources().getInteger(R.integer.zero));
							int lifeCost = (int) Math.pow(currentBoughtLife, 2d) * cost[index];
							//insertPrice(lifeCost);
							buy.setVisibility(View.VISIBLE);
						}
					});
				} else {
					buy.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							int currMoney = savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));
							if (currMoney >= cost[index]) {
								editor.putBoolean(shipsItem[index], true);
								editor.putInt(getString(R.string.saved_money), currMoney - cost[index]);
								editor.apply();
								buy.setClickable(false);
								buy.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.drawable_already_buy_button));
								//insertPrice(cost[index]);
							}
						}
					});
					buttonArrayList.add(buy);
					shipItemName.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for (Button b : buttonArrayList)
								b.setVisibility(View.GONE);
							//insertPrice(cost[index]);
							buy.setVisibility(View.VISIBLE);
						}
					});

					int rBool = shipsItem[i].equals(getString(R.string.ship_simple)) ? R.bool.vrai : R.bool.faux;
					if (savedShop.getBoolean(shipsItem[i], getResources().getBoolean(rBool))) {
						buy.setClickable(false);
						buy.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.drawable_already_buy_button));
					} else {
						buy.setClickable(true);
					}
				}
				subLayout.addView(buttons);
				buttons.setLayoutParams(layoutParams);
			}
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[page].equals(ShopFragmentPagerAdapter.BONUS_TAB)) {
			final String[] bonusItem = getResources().getStringArray(R.array.bonus_shop_list_item);
			final int[] cost = getResources().getIntArray(R.array.bonus_shop_price);
			for (int i = 0; i < bonusItem.length; i++) {
				final LinearLayout buttons = (LinearLayout) inflater.inflate(R.layout.expand_button, null);
				final TextView bonusItemName = (TextView) buttons.findViewById(R.id.expand_text);
				bonusItemName.setText(bonusItem[i]);
				final Button buy = (Button) buttons.findViewById(R.id.expand_button);
				buy.setText("$");
				final int index = i;
				if (index == 0) {
					buy.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							int currentBoughtDuration = savedShop.getInt(getString(R.string.bought_duration), getResources().getInteger(R.integer.zero));
							int currentPrice = (int) Math.pow(currentBoughtDuration / 10, 2d) * cost[index];
							int currMoney = savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));
							if (currMoney >= currentPrice) {
								editor.putInt(getString(R.string.bought_duration), currentBoughtDuration + 10);
								editor.putInt(getString(R.string.saved_money), currMoney - currentPrice);
								editor.commit();
								/*insertPrice((int) Math.pow(savedShop.getInt(getString(R.string.bought_duration), getResources().getInteger(R.integer.zero)) / 10, 2d) * getResources().getInteger(R.integer.life_coeff_cost));
								updateShipInfo();*/
							}
						}
					});
					buttonArrayList.add(buy);
					bonusItemName.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for (Button b : buttonArrayList)
								b.setVisibility(View.GONE);
							int currentBoughtDuration = savedShop.getInt(getString(R.string.bought_duration), getResources().getInteger(R.integer.zero));
							int lifeCost = (int) Math.pow(currentBoughtDuration / 10, 2d) * cost[index];
							//insertPrice(lifeCost);
							buy.setVisibility(View.VISIBLE);
						}
					});
				} else {
					buy.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							int currMoney = savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));
							if (currMoney >= cost[index]) {
								editor.putBoolean(bonusItem[index], true);
								editor.putInt(getString(R.string.saved_money), currMoney - cost[index]);
								editor.apply();
								buy.setClickable(false);
								buy.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.drawable_already_buy_button));
								//insertPrice(cost[index]);
							}
						}
					});
					buttonArrayList.add(buy);
					bonusItemName.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for (Button b : buttonArrayList)
								b.setVisibility(View.GONE);
							//insertPrice(cost[index]);
							buy.setVisibility(View.VISIBLE);
						}
					});

					int rBool = bonusItem[i].equals(getString(R.string.bonus_1)) ? R.bool.vrai : R.bool.faux;
					if (savedShop.getBoolean(bonusItem[i], getResources().getBoolean(rBool))) {
						buy.setClickable(false);
						buy.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.drawable_already_buy_button));
					} else {
						buy.setClickable(true);
					}
				}
				subLayout.addView(buttons);
				buttons.setLayoutParams(layoutParams);
			}
		}
		return linearLayout;

		//return inflater.inflate(R.layout.game_params, container, false);
	}
}
