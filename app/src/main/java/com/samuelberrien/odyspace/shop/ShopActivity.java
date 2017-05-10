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

import com.samuelberrien.odyspace.R;

public class ShopActivity extends AppCompatActivity {

    private String[] fireItem;

    private String currFireItem;
    private String currShipItem;
    private String currBonusItem;
    private int currPrice;

    private Button buyButton;
    private Button useButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.fireItem = getResources().getStringArray(R.array.fire_shop_list_item);

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

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void buy(View v) {
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_shop), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (!this.currFireItem.equals("")) {
            editor.putBoolean(this.currFireItem, true);
            editor.commit();
            this.buyButton.setVisibility(View.GONE);
            this.useButton.setText("Use It (" + this.currFireItem + ")");
            this.useButton.setVisibility(View.VISIBLE);
        } else if (!this.currShipItem.equals("")) {

        } else if (!this.currBonusItem.equals("")) {

        }
    }

    public void use(View v) {
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_ship_info), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (!this.currFireItem.equals("")) {
            editor.putString(getString(R.string.current_fire_type), this.currFireItem);
            editor.commit();
        } else if (!this.currShipItem.equals("")) {

        } else if (!this.currBonusItem.equals("")) {

        }
    }

    public void setItemChosen(int page, int id) {
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_shop), Context.MODE_PRIVATE);
        if (SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.FIRE_TAB) == 0) {
            if (this.fireItem[id].equals(getString(R.string.fire_bonus_1))) {
                this.fireTypeChosen(sharedPref, id, R.bool.saved_simple_fire_bought_default, R.string.fire_bonus_1, R.integer.simple_fire_cost);
            } else if (this.fireItem[id].equals(getString(R.string.fire_bonus_2))) {
                this.fireTypeChosen(sharedPref, id, R.bool.saved_quint_fire_bought_default, R.string.fire_bonus_2, R.integer.quint_fire_cost);
            } else {
                this.fireTypeChosen(sharedPref, id, R.bool.saved_simple_bomb_bought_default, R.string.fire_bonus_3, R.integer.simple_bomb_cost);
            }
        } else if (SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.SHIP_TAB) == 0) {

        } else if (SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.BONUS_TAB) == 0) {

        }
        sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_ship_info), Context.MODE_PRIVATE);
        String defaultValue = getString(R.string.saved_fire_type_default);
        System.out.println(sharedPref.getString(getString(R.string.current_fire_type), defaultValue));
    }

    private void fireTypeChosen(SharedPreferences sharedPref, int indexFire, int defaultFireResId, int fireResId, int fireCostRedId) {
        this.currFireItem = this.fireItem[indexFire];
        this.currShipItem = "";
        this.currBonusItem = "";
        boolean defaultValue = getResources().getBoolean(defaultFireResId);
        boolean currentPurchase = sharedPref.getBoolean(getString(fireResId), defaultValue);
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
}
