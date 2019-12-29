package com.samuelberrien.odyspace.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.samuelberrien.odyspace.R;

/**
 * Created by samuel on 12/10/17.
 */

public class SettingsFragment extends Fragment {


	private View gameSettings;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {

		gameSettings = inflater.inflate(R.layout.new_settings, container, false);

		((LinearLayout) gameSettings.findViewById(R.id.game_settings_main))
				.addView(new SettingsView(getActivity()));


		return gameSettings;
	}
}
