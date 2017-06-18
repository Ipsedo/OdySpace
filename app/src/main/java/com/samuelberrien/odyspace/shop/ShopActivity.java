package com.samuelberrien.odyspace.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;

public class ShopActivity extends AppCompatActivity {

    private String[] fireItem;
    private String[] shipItem;

    private String currFireItem;
    private String currShipItem;
    private String currBonusItem;
    private int currPrice;

    private Button buyButton;
    private Button useButton;

    private TextView currMoneyTextView;

    private SharedPreferences savedShop;
    private SharedPreferences savedShip;

    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.fireItem = getResources().getStringArray(R.array.fire_shop_list_item);
        this.shipItem = getResources().getStringArray(R.array.ship_shop_list_item);

        this.currFireItem = "";
        this.currShipItem = "";
        this.currBonusItem = "";
        this.currPrice = 0;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_shop);

        this.relativeLayout = (RelativeLayout) this.findViewById(R.id.activity_shop);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));

        this.buyButton = (Button) findViewById(R.id.buy_button);
        this.buyButton.setVisibility(View.GONE);
        this.buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShopActivity.this.buy();
                    }
                });
            }
        });
        //this.turnOffBuyButton();

        this.useButton = (Button) findViewById(R.id.use_item_button);
        this.useButton.setVisibility(View.GONE);
        //this.turnOffUseButton();
        this.useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShopActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShopActivity.this.use();
                    }
                });
            }
        });

        this.currMoneyTextView = (TextView) findViewById(R.id.shop_curr_money_text_view);
        this.savedShop = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_shop), Context.MODE_PRIVATE);
        int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
        int currMoney = this.savedShop.getInt(getString(R.string.saved_money), defaultMoney);
        this.currMoneyTextView.setText(Integer.toString(currMoney) + "$");

        this.savedShip = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_ship_info), Context.MODE_PRIVATE);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void buy() {
        SharedPreferences.Editor editor = this.savedShop.edit();

        int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
        int currMoney = this.savedShop.getInt(getString(R.string.saved_money), defaultMoney);

        if (!this.currFireItem.equals("") && currMoney >= this.currPrice) {
            editor.putBoolean(this.currFireItem, true);
            editor.putInt(getString(R.string.saved_money), currMoney - this.currPrice);
            editor.commit();
            this.buyButton.setVisibility(View.GONE);
            this.useButton.setText("Use It (" + this.currFireItem + ")");
            this.useButton.setVisibility(View.VISIBLE);
        } else if (!this.currShipItem.equals("") && currMoney >= this.currPrice) {
            if (this.currShipItem.equals(getString(R.string.bought_life))) {
                this.buyLife(editor, currMoney);
            } else {
                editor.putBoolean(this.currShipItem, true);
                editor.putInt(getString(R.string.saved_money), currMoney - this.currPrice);
                editor.commit();
                this.buyButton.setVisibility(View.GONE);
                this.useButton.setText("Use It (" + this.currShipItem + ")");
                this.useButton.setVisibility(View.VISIBLE);
            }
        } else if (!this.currBonusItem.equals("") && currMoney >= this.currPrice) {

        }

        currMoney = this.savedShop.getInt(getString(R.string.saved_money), defaultMoney);
        this.currMoneyTextView.setText(Integer.toString(currMoney) + "$");
    }

    private void buyLife(SharedPreferences.Editor editor, int currMoney) {
        int defaultValue = getResources().getInteger(R.integer.saved_ship_life_shop_default);
        int currentValue = this.savedShop.getInt(getString(R.string.bought_life), defaultValue);
        editor.putInt(getString(R.string.bought_life), currentValue + 1);
        editor.putInt(getString(R.string.saved_money), currMoney - this.currPrice);
        editor.commit();

        this.updateLifePrice();
    }

    public void use() {
        SharedPreferences.Editor editor = this.savedShip.edit();
        int boolResBought = R.bool.faux;

        if (!this.currFireItem.equals("")) {
            if (this.currFireItem.equals(getString(R.string.fire_bonus_1))) {
                boolResBought = R.bool.saved_simple_fire_bought_default;
            } else if (this.currFireItem.equals(getString(R.string.fire_bonus_2))) {
                boolResBought = R.bool.saved_quint_fire_bought_default;
            } else {
                boolResBought = R.bool.saved_quint_fire_bought_default;
            }
        } else if (!this.currShipItem.equals("")) {
            if (this.currShipItem.equals(getString(R.string.ship_bird))) {
                boolResBought = R.bool.saved_ship_bird_bought_default;
            } else if (this.currShipItem.equals(getString(R.string.ship_supreme))) {
                boolResBought = R.bool.saved_ship_supreme_bought_default;
            } else {
                boolResBought = R.bool.saved_ship_simple_bought_default;
            }
        } else if (!this.currBonusItem.equals("")) {
        }

        if (!this.currFireItem.equals("") && this.savedShop.getBoolean(this.currFireItem, getResources().getBoolean(boolResBought))) {
            editor.putString(getString(R.string.current_fire_type), this.currFireItem);
            editor.commit();
        } else if (!this.currShipItem.equals("") && this.savedShop.getBoolean(this.currShipItem, getResources().getBoolean(boolResBought))) {
            editor.putString(getString(R.string.current_ship_used), this.currShipItem);
            if (this.currShipItem.equals(getString(R.string.ship_bird))) {
                editor.putInt(getString(R.string.current_life_number), 50);
            } else if (this.currShipItem.equals(getString(R.string.ship_supreme))) {
                editor.putInt(getString(R.string.current_life_number), 200);
            } else {
                editor.putInt(getString(R.string.current_life_number), 20);
            }
            editor.commit();
        } else if (!this.currBonusItem.equals("") && this.savedShop.getBoolean(this.currBonusItem, getResources().getBoolean(boolResBought))) {

        }
    }

    public void setItemChosen(int page, int id) {
        this.useButton.setVisibility(View.GONE);
        this.buyButton.setVisibility(View.GONE);

        if (SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.FIRE_TAB) == 0) {
            this.fireTypeChosen(id);
        } else if (SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.SHIP_TAB) == 0) {
            this.shipItemChosen(id);
        } else if (SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.BONUS_TAB) == 0) {

        }
    }

    private void fireTypeChosen(int indexFire) {
        int defaultFireResId;
        int fireResId;
        int fireCostResId;
        if (this.fireItem[indexFire].equals(getString(R.string.fire_bonus_1))) {
            defaultFireResId = R.bool.saved_simple_fire_bought_default;
            fireResId = R.string.fire_bonus_1;
            fireCostResId = R.integer.simple_fire_cost;
        } else if (this.fireItem[indexFire].equals(getString(R.string.fire_bonus_2))) {
            defaultFireResId = R.bool.saved_quint_fire_bought_default;
            fireResId = R.string.fire_bonus_2;
            fireCostResId = R.integer.quint_fire_cost;
        } else {
            defaultFireResId = R.bool.saved_quint_fire_bought_default;
            fireResId = R.string.fire_bonus_3;
            fireCostResId = R.integer.simple_bomb_cost;
        }

        boolean defaultValue = getResources().getBoolean(defaultFireResId);
        boolean currentPurchase = this.savedShop.getBoolean(getString(fireResId), defaultValue);
        if (!currentPurchase) {
            this.currPrice = getResources().getInteger(fireCostResId);
            this.buyButton.setText("Buy It (" + this.fireItem[indexFire] + " " + this.currPrice + "$)");
            this.buyButton.setVisibility(View.VISIBLE);
        } else {
            this.useButton.setText("Use It (" + this.fireItem[indexFire] + ")");
            this.useButton.setVisibility(View.VISIBLE);
        }

        this.currFireItem = this.fireItem[indexFire];
        this.currShipItem = "";
        this.currBonusItem = "";
    }

    private void updateLifePrice() {
        int defaultValue = getResources().getInteger(R.integer.saved_ship_life_shop_default);
        int currentValue = this.savedShop.getInt(getString(R.string.bought_life), defaultValue);
        this.currPrice = (int) Math.pow(currentValue, 2d) * getResources().getInteger(R.integer.life_coeff_cost);
        this.buyButton.setText("Buy It (" + getString(R.string.bought_life) + " " + this.currPrice + "$)");
    }

    private void shipItemChosen(int id) {
        if (this.shipItem[id].equals(getString(R.string.bought_life))) {
            this.updateLifePrice();
            this.buyButton.setText("Buy It (" + this.shipItem[id] + " " + this.currPrice + "$)");
            this.buyButton.setVisibility(View.VISIBLE);

            this.currShipItem = this.shipItem[id];
            this.currBonusItem = "";
            this.currFireItem = "";
        } else {
            int defaultItemResId;
            int itemResId;
            int itemCostResId;
            if (this.shipItem[id].equals(getString(R.string.ship_bird))) {
                defaultItemResId = R.bool.saved_ship_bird_bought_default;
                itemResId = R.string.ship_bird;
                itemCostResId = R.integer.ship_bird_cost;
            } else if (this.shipItem[id].equals(getString(R.string.ship_supreme))) {
                defaultItemResId = R.bool.saved_ship_supreme_bought_default;
                itemResId = R.string.ship_supreme;
                itemCostResId = R.integer.ship_supreme_cost;
            } else {
                defaultItemResId = R.bool.saved_ship_simple_bought_default;
                itemResId = R.string.ship_simple;
                itemCostResId = R.integer.ship_simple_cost;
            }

            boolean defaultValue = getResources().getBoolean(defaultItemResId);
            boolean currentPurchase = this.savedShop.getBoolean(getString(itemResId), defaultValue);

            if (!currentPurchase) {
                this.currPrice = getResources().getInteger(itemCostResId);
                this.buyButton.setText("Buy It (" + this.shipItem[id] + " " + this.currPrice + "$)");
                this.buyButton.setVisibility(View.VISIBLE);
            } else {
                this.useButton.setText("Use It (" + this.shipItem[id] + ")");
                this.useButton.setVisibility(View.VISIBLE);
            }

            this.currShipItem = this.shipItem[id];
            this.currBonusItem = "";
            this.currFireItem = "";
        }
    }
}
