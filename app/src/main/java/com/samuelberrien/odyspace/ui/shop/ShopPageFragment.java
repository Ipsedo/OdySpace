package com.samuelberrien.odyspace.ui.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by samuel on 04/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class ShopPageFragment extends Fragment {

	private static final String ARG_PAGE = "ARG_PAGE";

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
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//return inflater.inflate(R.layout.game_params, container, false);
		//return ((ShopActivity) getActivity()).setPageChosen(this.mPage - 1, inflater, container);
		return ((ShopFragment) getParentFragment()).setPageChosen(this.mPage - 1, inflater, container);
	}
}
