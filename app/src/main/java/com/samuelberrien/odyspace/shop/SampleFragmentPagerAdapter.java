package com.samuelberrien.odyspace.shop;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by samuel on 04/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

	final int PAGE_COUNT = 3;
	public static String SHIP_TAB = "Ship";
	public static String FIRE_TAB = "Fire";
	public static String BONUS_TAB = "Bonus";
	public static String[] TAB_TITLES = new String[]{SHIP_TAB, FIRE_TAB, BONUS_TAB};

	public SampleFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public int getCount() {
		return PAGE_COUNT;
	}

	@Override
	public Fragment getItem(int position) {
		return PageFragment.newInstance(position + 1);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// Generate title based on item position
		return TAB_TITLES[position];
	}

}
