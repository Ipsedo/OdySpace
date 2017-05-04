package com.samuelberrien.odyspace.shop;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.samuelberrien.odyspace.R;

public class ShopActivity extends AppCompatActivity {

    private String[] fireItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.fireItem = getResources().getStringArray(R.array.fire_shop_list_item);

        setContentView(R.layout.activity_shop);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), ShopActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setItemChosen(int page, int id) {
        if(SampleFragmentPagerAdapter.TAB_TITLES[page].compareTo(SampleFragmentPagerAdapter.FIRE_TAB) == 0) {
            System.out.println(fireItem[id]);
        }
    }

    public void ship(View v) {

    }

    public void fire(View v) {

    }
}
