package com.samuelberrien.odyspace.shop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.samuelberrien.odyspace.R;

/**
 * Created by samuel on 04/05/17.
 * Copyright samuel, 2016 - 2017.
 * Toute reproduction ou utilisation sans l'autorisation
 * de l'auteur engendrera des poursuites judiciaires.
 */

public class PageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
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
        if (SampleFragmentPagerAdapter.TAB_TITLES[this.mPage - 1].compareTo(SampleFragmentPagerAdapter.FIRE_TAB) == 0) {
            listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.shop_text_view, getResources().getStringArray(R.array.fire_shop_list_item)));
        } else if (SampleFragmentPagerAdapter.TAB_TITLES[this.mPage - 1].compareTo(SampleFragmentPagerAdapter.SHIP_TAB) == 0) {
            listView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.shop_text_view, getResources().getStringArray(R.array.ship_shop_list_item)));
        } else if (SampleFragmentPagerAdapter.TAB_TITLES[this.mPage - 1].compareTo(SampleFragmentPagerAdapter.BONUS_TAB) == 0) {

        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new Thread() {
                    public void run() {
                        PageFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((ShopActivity) PageFragment.this.getActivity()).setItemChosen(PageFragment.this.mPage - 1, i);
                            }
                        });
                    }
                }.start();
            }
        });
        /*listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((ShopActivity) PageFragment.this.getActivity()).setItemChosen(PageFragment.this.mPage - 1, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/
        listView.setClickable(true);

        return view;
    }
}
