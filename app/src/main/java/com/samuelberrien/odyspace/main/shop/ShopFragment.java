package com.samuelberrien.odyspace.main.shop;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.game.Purchases;

/**
 * Created by samuel on 12/10/17.
 */

public class ShopFragment extends Fragment {

	private SharedPreferences savedShop;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		savedShop = getActivity().getApplicationContext()
				.getSharedPreferences(getString(R.string.shop_preferences), Context.MODE_PRIVATE);

		View view = inflater.inflate(R.layout.new_shop, container, false);
		ViewPager viewPager = (ViewPager) view.findViewById(R.id.shop_view_pager);
		viewPager.setAdapter(new ShopFragmentPagerAdapter(getChildFragmentManager()));

		TabLayout tabLayout = (TabLayout) view.findViewById(R.id.shop_tab_layout);
		tabLayout.setupWithViewPager(viewPager);
		return view;
	}

	public View setPageChosen(int page, LayoutInflater inflater, ViewGroup container) {

		LinearLayout linearLayout = (LinearLayout) inflater.inflate(
				R.layout.purchase_list, container, false).findViewById(R.id.layout_list_purchase);
		LinearLayout subLayout = (LinearLayout) linearLayout.findViewById(
				R.id.layout_list_purchase_scroll);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, getResources().getDisplayMetrics());
		layoutParams.setMargins(margin, margin, margin, 0);

		if (ShopFragmentPagerAdapter.TAB_TITLES[page]
				.equals(ShopFragmentPagerAdapter.FIRE_TAB)) {
			String[] fires = getResources().getStringArray(R.array.fire_shop_list_item);
			for (int i = 0; i < fires.length; i++) {
				subLayout.addView(new ShopItemView(getContext(), Purchases.FIRE, i));
			}
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[page]
				.equals(ShopFragmentPagerAdapter.SHIP_TAB)) {
			String[] shipsItem = getResources().getStringArray(R.array.ship_shop_list_item);
			for (int i = 0; i < shipsItem.length; i++) {
				subLayout.addView(new ShopItemView(getContext(), Purchases.SHIP, i));
			}
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[page]
				.equals(ShopFragmentPagerAdapter.BONUS_TAB)) {
			String[] bonusItem = getResources().getStringArray(R.array.bonus_shop_list_item);
			for (int i = 0; i < bonusItem.length; i++) {
				subLayout.addView(new ShopItemView(getContext(), Purchases.BONUS, i));
			}
		}
		return linearLayout;
	}
}
