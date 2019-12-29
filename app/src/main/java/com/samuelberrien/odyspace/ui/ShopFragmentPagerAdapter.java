package com.samuelberrien.odyspace.ui;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.samuelberrien.odyspace.core.Purchases;

/**
 * Created by samuel on 04/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

class ShopFragmentPagerAdapter extends FragmentPagerAdapter {

	static String SHIP_TAB = Purchases.SHIP.getName();
	static String FIRE_TAB = Purchases.FIRE.getName();
	static String BONUS_TAB = Purchases.BONUS.getName();
	static String[] TAB_TITLES = new String[]{SHIP_TAB, FIRE_TAB, BONUS_TAB};

	ShopFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Fragment getItem(int position) {
		return ShopPageFragment.newInstance(position + 1);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// Generate title based on item position
		return TAB_TITLES[position];
	}

}
