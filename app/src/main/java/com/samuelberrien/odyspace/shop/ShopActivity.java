package com.samuelberrien.odyspace.shop;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.game.Purchases;
import com.samuelberrien.odyspace.utils.main.ItemImageViewMaker;
import com.samuelberrien.odyspace.utils.main.ViewHelper;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {

	private TextView currMoneyTextView;

	private SharedPreferences savedShop;
	private SharedPreferences savedShip;

	private Toast myToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_shop);

		ViewPager viewPager = (ViewPager) findViewById(R.id.shop_view_pager);
		viewPager.setAdapter(new ShopFragmentPagerAdapter(getSupportFragmentManager()));

		this.currMoneyTextView = (TextView) findViewById(R.id.shop_curr_money_text_view);
		this.savedShop = this.getApplicationContext().getSharedPreferences(getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
		int currMoney = this.savedShop.getInt(getString(R.string.saved_money), defaultMoney);
		this.currMoneyTextView.setText(Integer.toString(currMoney).concat(" $"));

		this.savedShip = this.getApplicationContext().getSharedPreferences(getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);

		this.myToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);

		this.updateShipInfo();

		TabLayout tabLayout = (TabLayout) findViewById(R.id.shop_tab_layout);
		tabLayout.setupWithViewPager(viewPager);
	}

	private void updateShipInfo() {
		final String currFireType = this.savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default));

		final int currShipLife = this.savedShip.getInt(getString(R.string.current_life_number), getResources().getInteger(R.integer.saved_ship_life_default));

		final int currBoughtLife = this.savedShop.getInt(getString(R.string.bought_life), getResources().getInteger(R.integer.saved_ship_life_shop_default));

		final String shipUsed = this.savedShip.getString(getString(R.string.current_ship_used), getString(R.string.saved_ship_used_default));

		final String bonusUsed = this.savedShip.getString(getString(R.string.current_bonus_used), getString(R.string.bonus_1));
		final int currBonusDuration = this.savedShip.getInt(getString(R.string.current_bonus_duration), getResources().getInteger(R.integer.bonus_1_duration));
		final int currBoughtDuration = this.savedShop.getInt(getString(R.string.bought_duration), 0);

		final ImageView imageView1 = (ImageView) findViewById(R.id.fire_image_shop);
		ItemImageViewMaker.makeFireTypeImage(this, this.myToast, imageView1, currFireType);
		imageView1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewHelper.makeViewTransition(ShopActivity.this, imageView1);
				showDialog(ItemImageViewMaker.makeSelectItemView(ShopActivity.this, Purchases.FIRE));
			}
		});

		final ImageView imageView2 = (ImageView) findViewById(R.id.ship_image_shop);
		ItemImageViewMaker.makeShipImage(this, this.myToast, imageView2, shipUsed, currShipLife, currBoughtLife);
		imageView2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewHelper.makeViewTransition(ShopActivity.this, imageView2);
				showDialog(ItemImageViewMaker.makeSelectItemView(ShopActivity.this, Purchases.SHIP));
			}
		});

		final ImageView imageView3 = (ImageView) findViewById(R.id.bonus_image_shop);
		ItemImageViewMaker.makeBonusImage(this, this.myToast, imageView3, bonusUsed, currBonusDuration, currBoughtDuration);
		imageView3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewHelper.makeViewTransition(ShopActivity.this, imageView3);
				showDialog(ItemImageViewMaker.makeSelectItemView(ShopActivity.this, Purchases.BONUS));
			}
		});
	}

	private void showDialog(View v) {
		AlertDialog pauseDialog = new AlertDialog.Builder(this)
				.setTitle("Item chooser")
				.setView(v)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
					}
				})
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialogInterface) {
						updateShipInfo();
					}
				})
				.create();
		pauseDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.button_main));
		pauseDialog.show();
	}

	private void insertPrice(int price) {
		int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
		int currMoney = this.savedShop.getInt(getString(R.string.saved_money), defaultMoney);
		this.currMoneyTextView.setText(Integer.toString(currMoney).concat(" (-" + price + ") $"));
	}

	public View setPageChosen(int page, LayoutInflater inflater, ViewGroup container) {
		LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.purchase_list, container, false).findViewById(R.id.layout_list_purchase);
		LinearLayout subLayout = (LinearLayout) linearLayout.findViewById(R.id.layout_list_purchase_scroll);

		final ArrayList<Button> buttonArrayList = new ArrayList<>();

		final SharedPreferences.Editor editor = this.savedShop.edit();

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
		layoutParams.setMargins(margin, margin, margin, 0);

		if (ShopFragmentPagerAdapter.TAB_TITLES[page].compareTo(ShopFragmentPagerAdapter.FIRE_TAB) == 0) {
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
							buy.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.drawable_already_buy_button));
							insertPrice(cost[index]);
						}
					}
				});
				buttonArrayList.add(buy);
				fireName.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						for (Button b : buttonArrayList)
							b.setVisibility(View.GONE);
						insertPrice(cost[index]);
						buy.setVisibility(View.VISIBLE);
					}
				});

				int rBool = fires[i].equals(getString(R.string.fire_1)) ? R.bool.vrai : R.bool.faux;
				if (savedShop.getBoolean(fires[i], getResources().getBoolean(rBool))) {
					buy.setClickable(false);
					buy.setBackground(ContextCompat.getDrawable(this, R.drawable.drawable_already_buy_button));
				} else {
					buy.setClickable(true);
				}
				subLayout.addView(buttons);
				buttons.setLayoutParams(layoutParams);
			}
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[page].compareTo(ShopFragmentPagerAdapter.SHIP_TAB) == 0) {
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
								insertPrice(lifeCost);
								updateShipInfo();
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
							insertPrice(lifeCost);
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
								buy.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.drawable_already_buy_button));
								insertPrice(cost[index]);
							}
						}
					});
					buttonArrayList.add(buy);
					shipItemName.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for (Button b : buttonArrayList)
								b.setVisibility(View.GONE);
							insertPrice(cost[index]);
							buy.setVisibility(View.VISIBLE);
						}
					});

					int rBool = shipsItem[i].equals(getString(R.string.ship_simple)) ? R.bool.vrai : R.bool.faux;
					if (savedShop.getBoolean(shipsItem[i], getResources().getBoolean(rBool))) {
						buy.setClickable(false);
						buy.setBackground(ContextCompat.getDrawable(this, R.drawable.drawable_already_buy_button));
					} else {
						buy.setClickable(true);
					}
				}
				subLayout.addView(buttons);
				buttons.setLayoutParams(layoutParams);
			}
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[page].compareTo(ShopFragmentPagerAdapter.BONUS_TAB) == 0) {
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
								insertPrice((int) Math.pow(savedShop.getInt(getString(R.string.bought_duration), getResources().getInteger(R.integer.zero)) / 10, 2d) * getResources().getInteger(R.integer.life_coeff_cost));
								updateShipInfo();
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
							insertPrice(lifeCost);
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
								buy.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.drawable_already_buy_button));
								insertPrice(cost[index]);
							}
						}
					});
					buttonArrayList.add(buy);
					bonusItemName.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							for (Button b : buttonArrayList)
								b.setVisibility(View.GONE);
							insertPrice(cost[index]);
							buy.setVisibility(View.VISIBLE);
						}
					});

					int rBool = bonusItem[i].equals(getString(R.string.bonus_1)) ? R.bool.vrai : R.bool.faux;
					if (savedShop.getBoolean(bonusItem[i], getResources().getBoolean(rBool))) {
						buy.setClickable(false);
						buy.setBackground(ContextCompat.getDrawable(this, R.drawable.drawable_already_buy_button));
					} else {
						buy.setClickable(true);
					}
				}
				subLayout.addView(buttons);
				buttons.setLayoutParams(layoutParams);
			}
		}
		return linearLayout;
	}
}
