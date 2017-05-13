package com.samuelberrien.odyspace.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.game.LevelActivity;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_shop);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), ShopActivity.this));

        this.buyButton = (Button) findViewById(R.id.buy_button);
        this.buyButton.setVisibility(View.GONE);
        this.useButton = (Button) findViewById(R.id.use_item_button);
        this.useButton.setVisibility(View.GONE);

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

    public void buy(View v) {
        SharedPreferences.Editor editor = this.savedShop.edit();

        int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
        int currMoney = this.savedShop.getInt(getString(R.string.saved_money), defaultMoney);

        if (!this.currFireItem.equals("") && currMoney > this.currPrice) {
            editor.putBoolean(this.currFireItem, true);
            editor.putInt(getString(R.string.saved_money), currMoney - this.currPrice);
            editor.commit();
            this.buyButton.setVisibility(View.GONE);
            this.useButton.setText("Use It (" + this.currFireItem + ")");
            this.useButton.setVisibility(View.VISIBLE);
        } else if (!this.currShipItem.equals("") && currMoney > this.currPrice) {
            if (this.currShipItem.equals(getString(R.string.bought_life))) {
                this.buyLife(editor, currMoney);
            }
        } else if (!this.currBonusItem.equals("") && currMoney > this.currPrice) {

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

        SharedPreferences sharedPrefShip = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_ship_info), Context.MODE_PRIVATE);
        int defaultSavedShipLife = getResources().getInteger(R.integer.saved_ship_life_default);
        int currShipLife = sharedPrefShip.getInt(getString(R.string.current_life_number), defaultSavedShipLife);
        SharedPreferences.Editor editorShip = sharedPrefShip.edit();
        editorShip.putInt(getString(R.string.current_life_number), currShipLife + 1);
        editorShip.commit();

        this.updateLifePrice();
    }

    public void use(View v) {
        SharedPreferences.Editor editor = this.savedShip.edit();
        if (!this.currFireItem.equals("")) {
            editor.putString(getString(R.string.current_fire_type), this.currFireItem);
            editor.commit();
        } else if (!this.currShipItem.equals("")) {

        } else if (!this.currBonusItem.equals("")) {

        }
    }

    public void setItemChosen(int page, int id) {
        if (SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.FIRE_TAB) == 0) {
            this.fireTypeChosen(id);
        } else if (SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.SHIP_TAB) == 0) {
            this.shipItemChosen(id);
        } else if (SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.BONUS_TAB) == 0) {

        }
    }

    private void fireTypeChosen(int indexFire) {
        this.currFireItem = this.fireItem[indexFire];
        this.currShipItem = "";
        this.currBonusItem = "";

        int defaultFireResId;
        int fireResId;
        int fireCostRedId;
        if (this.fireItem[indexFire].equals(getString(R.string.fire_bonus_1))) {
            defaultFireResId = R.bool.saved_simple_fire_bought_default;
            fireResId = R.string.fire_bonus_1;
            fireCostRedId = R.integer.simple_fire_cost;
        } else if (this.fireItem[indexFire].equals(getString(R.string.fire_bonus_2))) {
            defaultFireResId = R.bool.saved_quint_fire_bought_default;
            fireResId = R.string.fire_bonus_2;
            fireCostRedId = R.integer.quint_fire_cost;
        } else {
            defaultFireResId = R.bool.saved_quint_fire_bought_default;
            fireResId = R.string.fire_bonus_3;
            fireCostRedId = R.integer.simple_bomb_cost;
        }

        boolean defaultValue = getResources().getBoolean(defaultFireResId);
        boolean currentPurchase = this.savedShop.getBoolean(getString(fireResId), defaultValue);
        if (!currentPurchase) {
            this.useButton.setVisibility(View.GONE);
            this.buyButton.setVisibility(View.VISIBLE);
            this.currPrice = getResources().getInteger(fireCostRedId);
            this.buyButton.setText("Buy It (" + this.fireItem[indexFire] + " " + this.currPrice + "$)");
        } else {
            this.buyButton.setVisibility(View.GONE);
            this.useButton.setVisibility(View.VISIBLE);
            this.useButton.setText("Use It (" + this.fireItem[indexFire] + ")");
        }
    }

    private void updateLifePrice() {
        int defaultValue = getResources().getInteger(R.integer.saved_ship_life_shop_default);
        int currentValue = this.savedShop.getInt(getString(R.string.bought_life), defaultValue);
        this.currPrice = (int) Math.pow(currentValue, 2d) * getResources().getInteger(R.integer.life_coeff_cost);
        this.buyButton.setText("Buy It (" + getString(R.string.bought_life) + " " + this.currPrice + "$)");
    }

    private void shipItemChosen(int id) {
        if (this.shipItem[id].equals(getString(R.string.bought_life))) {
            this.currShipItem = this.shipItem[id];
            this.currBonusItem = "";
            this.currFireItem = "";

            this.updateLifePrice();
            this.useButton.setVisibility(View.GONE);
            this.buyButton.setVisibility(View.VISIBLE);
            this.buyButton.setText("Buy It (" + this.shipItem[id] + " " + this.currPrice + "$)");
        }
    }
}
