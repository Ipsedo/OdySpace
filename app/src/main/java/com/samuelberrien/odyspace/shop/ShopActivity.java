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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.game.Purchases;
import com.samuelberrien.odyspace.utils.main.ItemImageViewMaker;
import com.samuelberrien.odyspace.utils.main.ViewHelper;

public class ShopActivity extends AppCompatActivity {

	private String[] fireItem;
	private String[] shipItem;
	private String[] bonusItem;

	private String currFireItem;
	private String currShipItem;
	private String currBonusItem;
	private int currPrice;

	private Button buyButton;

	private TextView currMoneyTextView;

	private SharedPreferences savedShop;
	private SharedPreferences savedShip;

	private Toast myToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.fireItem = getResources().getStringArray(R.array.fire_shop_list_item);
		this.shipItem = getResources().getStringArray(R.array.ship_shop_list_item);
		this.bonusItem = getResources().getStringArray(R.array.bonus_shop_list_item);

		this.currFireItem = "";
		this.currShipItem = "";
		this.currBonusItem = "";
		this.currPrice = 0;

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_shop);

		ViewPager viewPager = (ViewPager) findViewById(R.id.shop_view_pager);
		viewPager.setAdapter(new ShopFragmentPagerAdapter(getSupportFragmentManager()));

		this.buyButton = (Button) findViewById(R.id.buy_button);
		this.buyButton.setVisibility(View.GONE);
		this.buyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewHelper.makeViewTransition(ShopActivity.this, ShopActivity.this.buyButton);
				ShopActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ShopActivity.this.buy();
					}
				});
			}
		});

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

	public void buy() {
		SharedPreferences.Editor editor = this.savedShop.edit();

		int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
		int currMoney = this.savedShop.getInt(getString(R.string.saved_money), defaultMoney);

		if (!this.currFireItem.equals("") && currMoney >= this.currPrice) {
			editor.putBoolean(this.currFireItem, true);
			editor.putInt(getString(R.string.saved_money), currMoney - this.currPrice);
			editor.apply();
			this.buyButton.setVisibility(View.GONE);
		} else if (!this.currShipItem.equals("") && currMoney >= this.currPrice) {
			if (this.currShipItem.equals(getString(R.string.bought_life))) {
				this.buyLife(editor, currMoney);
			} else {
				editor.putBoolean(this.currShipItem, true);
				editor.putInt(getString(R.string.saved_money), currMoney - this.currPrice);
				editor.commit();
				this.buyButton.setVisibility(View.GONE);
			}
		} else if (!this.currBonusItem.equals("") && currMoney >= this.currPrice) {
			if (this.currBonusItem.equals(getString(R.string.bought_duration))) {
				this.buyDuration(editor, currMoney);
			} else {
				editor.putBoolean(this.currBonusItem, true);
				editor.putInt(getString(R.string.saved_money), currMoney - this.currPrice);
				editor.commit();
				this.buyButton.setVisibility(View.GONE);
			}
		} else {
			this.myToast.setText("Can't buy it !");
			this.myToast.show();
		}

		currMoney = this.savedShop.getInt(getString(R.string.saved_money), defaultMoney);
		this.currMoneyTextView.setText(Integer.toString(currMoney).concat(" $"));

		this.updateShipInfo();
	}

	private void buyLife(SharedPreferences.Editor editor, int currMoney) {
		int defaultValue = getResources().getInteger(R.integer.zero);
		int currentValue = this.savedShop.getInt(getString(R.string.bought_life), defaultValue);
		editor.putInt(getString(R.string.bought_life), currentValue + 1);
		editor.putInt(getString(R.string.saved_money), currMoney - this.currPrice);
		editor.commit();

		this.updateLifePrice();
	}

	private void buyDuration(SharedPreferences.Editor editor, int currMoney) {
		int defaultValue = getResources().getInteger(R.integer.zero);
		int currentValue = this.savedShop.getInt(getString(R.string.bought_duration), defaultValue);
		editor.putInt(getString(R.string.bought_duration), currentValue + 10);
		editor.putInt(getString(R.string.saved_money), currMoney - this.currPrice);
		editor.commit();

		this.updateDurationPrice();
	}

	public void setItemChosen(int page, int id) {
		this.buyButton.setVisibility(View.GONE);

		if (ShopFragmentPagerAdapter.TAB_TITLES[page].compareTo(ShopFragmentPagerAdapter.FIRE_TAB) == 0) {
			this.fireTypeChosen(id);
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[page].compareTo(ShopFragmentPagerAdapter.SHIP_TAB) == 0) {
			this.shipItemChosen(id);
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[page].compareTo(ShopFragmentPagerAdapter.BONUS_TAB) == 0) {
			this.bonusItemChosen(id);
		}
	}

	private void fireTypeChosen(int indexFire) {
		int defaultFireResId = R.bool.faux;
		int fireResId;
		int fireCostResId;
		if (this.fireItem[indexFire].equals(getString(R.string.fire_1))) {
			defaultFireResId = R.bool.vrai;
			fireResId = R.string.fire_1;
			fireCostResId = R.integer.zero;
		} else if (this.fireItem[indexFire].equals(getString(R.string.fire_2))) {
			fireResId = R.string.fire_2;
			fireCostResId = R.integer.quint_fire_cost;
		} else if (this.fireItem[indexFire].equals(getString(R.string.fire_3))) {
			fireResId = R.string.fire_3;
			fireCostResId = R.integer.simple_bomb_cost;
		} else if (this.fireItem[indexFire].equals(getString(R.string.fire_4))) {
			fireResId = R.string.fire_4;
			fireCostResId = R.integer.triple_fire_cost;
		} else if (this.fireItem[indexFire].equals(getString(R.string.fire_5))) {
			fireResId = R.string.fire_5;
			fireCostResId = R.integer.laser_cost;
		} else {
			fireResId = R.string.fire_6;
			fireCostResId = R.integer.torus_cost;
		}

		boolean defaultValue = getResources().getBoolean(defaultFireResId);
		boolean currentPurchase = this.savedShop.getBoolean(getString(fireResId), defaultValue);
		if (!currentPurchase) {
			this.currPrice = getResources().getInteger(fireCostResId);
			this.buyButton.setText("BUY IT (" + this.fireItem[indexFire] + " " + this.currPrice + "$)");
			this.buyButton.setVisibility(View.VISIBLE);
		}

		this.currFireItem = this.fireItem[indexFire];
		this.currShipItem = "";
		this.currBonusItem = "";
	}

	private void updateLifePrice() {
		int defaultValue = getResources().getInteger(R.integer.zero);
		int currentValue = this.savedShop.getInt(getString(R.string.bought_life), defaultValue);
		this.currPrice = (int) Math.pow(currentValue, 2d) * getResources().getInteger(R.integer.life_coeff_cost);
		this.buyButton.setText("BUY IT (" + getString(R.string.bought_life) + " " + this.currPrice + "$)");
		this.buyButton.setVisibility(View.VISIBLE);
	}

	private void shipItemChosen(int id) {
		if (this.shipItem[id].equals(getString(R.string.bought_life))) {
			this.updateLifePrice();
		} else {
			int defaultItemResId = R.bool.faux;
			int itemResId;
			int itemCostResId;
			if (this.shipItem[id].equals(getString(R.string.ship_bird))) {
				itemResId = R.string.ship_bird;
				itemCostResId = R.integer.ship_bird_cost;
			} else if (this.shipItem[id].equals(getString(R.string.ship_supreme))) {
				itemResId = R.string.ship_supreme;
				itemCostResId = R.integer.ship_supreme_cost;
			} else {
				defaultItemResId = R.bool.vrai;
				itemResId = R.string.ship_simple;
				itemCostResId = R.integer.zero;
			}

			boolean defaultValue = getResources().getBoolean(defaultItemResId);
			boolean currentPurchase = this.savedShop.getBoolean(getString(itemResId), defaultValue);

			if (!currentPurchase) {
				this.currPrice = getResources().getInteger(itemCostResId);
				this.buyButton.setText("BUY IT (" + this.shipItem[id] + " " + this.currPrice + "$)");
				this.buyButton.setVisibility(View.VISIBLE);
			}
		}
		this.currShipItem = this.shipItem[id];
		this.currBonusItem = "";
		this.currFireItem = "";
	}

	private void updateDurationPrice() {
		int defaultValue = getResources().getInteger(R.integer.zero);
		int currentValue = this.savedShop.getInt(getString(R.string.bought_duration), defaultValue);
		this.currPrice = (int) Math.pow(currentValue, 2d) * getResources().getInteger(R.integer.life_coeff_cost);
		this.buyButton.setText("BUY IT (" + getString(R.string.bought_duration) + " " + this.currPrice + "$)");
		this.buyButton.setVisibility(View.VISIBLE);
	}

	private void bonusItemChosen(int id) {
		if (this.bonusItem[id].equals(getString(R.string.bought_duration))) {
			this.updateDurationPrice();
		} else {
			int defaultItemResId = R.bool.faux;
			int itemResId;
			int itemCostResId;

			if (this.bonusItem[id].equals(getString(R.string.bonus_1))) {
				defaultItemResId = R.bool.vrai;
				itemResId = R.string.bonus_1;
				itemCostResId = R.integer.zero;
			} else {
				itemResId = R.string.bonus_2;
				itemCostResId = R.integer.bonus_2_cost;
			}

			boolean defaultValue = getResources().getBoolean(defaultItemResId);
			boolean currentPurchase = this.savedShop.getBoolean(getString(itemResId), defaultValue);

			if (!currentPurchase) {
				this.currPrice = getResources().getInteger(itemCostResId);
				this.buyButton.setText("BUY IT (" + this.bonusItem[id] + " " + this.currPrice + "$)");
				this.buyButton.setVisibility(View.VISIBLE);
			}
		}

		this.currShipItem = "";
		this.currBonusItem = this.bonusItem[id];
		this.currFireItem = "";
	}
}
