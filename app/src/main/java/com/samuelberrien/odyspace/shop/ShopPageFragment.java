package com.samuelberrien.odyspace.shop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.drawable.maps.Map;

/**
 * Created by samuel on 04/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ShopPageFragment extends Fragment {

	public static final String ARG_PAGE = "ARG_PAGE";

	private int mPage;

	public static ShopPageFragment newInstance(int page) {
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, page);
		ShopPageFragment fragment = new ShopPageFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mPage = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_page, container, false);

		ListView listView = (ListView) view;
		if (ShopFragmentPagerAdapter.TAB_TITLES[this.mPage - 1].compareTo(ShopFragmentPagerAdapter.FIRE_TAB) == 0) {
			//listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.shop_text_view, getResources().getStringArray(R.array.fire_shop_list_item)));
			view = ((ShopActivity) getActivity()).setPageChosen(this.mPage - 1, inflater, container);
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[this.mPage - 1].compareTo(ShopFragmentPagerAdapter.SHIP_TAB) == 0) {
			//listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.shop_text_view, getResources().getStringArray(R.array.ship_shop_list_item)));
		} else if (ShopFragmentPagerAdapter.TAB_TITLES[this.mPage - 1].compareTo(ShopFragmentPagerAdapter.BONUS_TAB) == 0) {
			//listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.shop_text_view, getResources().getStringArray(R.array.bonus_shop_list_item)));
		}
		/*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
				ShopPageFragment.this.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						((ShopActivity) ShopPageFragment.this.getActivity()).setItemChosen(ShopPageFragment.this.mPage - 1, i);
					}
				});
			}
		});
		listView.setClickable(true);*/

		return view;
	}
}
