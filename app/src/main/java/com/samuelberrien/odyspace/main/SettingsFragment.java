package com.samuelberrien.odyspace.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.utils.main.GameParamsView;

/**
 * Created by samuel on 12/10/17.
 */

public class SettingsFragment extends Fragment {

	private SharedPreferences gamePreferences;

	private View gameSettings;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		gameSettings = inflater.inflate(R.layout.new_settings, container, false);

		gamePreferences = getActivity().getSharedPreferences(getString(R.string.game_preferences), Context.MODE_PRIVATE);

		//GameParamsView.buildGameParams(getActivity(), gameSettings, gamePreferences);

		((LinearLayout) gameSettings.findViewById(R.id.game_settings_main))
				.addView(new GameParamsView(getActivity()));


		return gameSettings;
	}
}
