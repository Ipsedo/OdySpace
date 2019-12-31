package com.samuelberrien.odyspace.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.core.Level;
import com.samuelberrien.odyspace.game.LevelActivity;
import com.samuelberrien.odyspace.utils.widget.ExpandButton;
import com.samuelberrien.odyspace.utils.widget.RadioExpand;

/**
 * Created by samuel on 12/10/17.
 */

public class LevelsFragment
		extends Fragment
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	private SharedPreferences savedLevelInfo;
	private RadioExpand radioExpand;
	/**
	 * needed for non-attached case
	 */
	private Context context;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		context = getContext();
		savedLevelInfo = context.getSharedPreferences(getString(R.string.level_info_preferences), Context.MODE_PRIVATE);

		View v = inflater.inflate(R.layout.new_levels, container, false);

		radioExpand = v.findViewById(R.id.radio_expand_level);

		updateLevelList();

		savedLevelInfo.registerOnSharedPreferenceChangeListener(this);

		return v;
	}

	private void updateLevelList() {
		int defaultValue = context.getResources().getInteger(R.integer.saved_max_level_default);
		int maxLevel = savedLevelInfo.getInt(context.getString(R.string.saved_max_level), defaultValue);

		radioExpand.removeAllViews();

		for (int i = 0; i <= maxLevel && i < Level.LEVELS.length; i++) {
			final int indexLevel = i;

			ExpandButton expandButton = new ExpandButton(context, () -> {
				Intent intent = new Intent(getActivity(), LevelActivity.class);
				intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(indexLevel));
				getActivity().startActivityForResult(intent, MainActivity.RESULT_VALUE);
			});
			expandButton.setText((i + 1) + " - " + Level.LEVELS[i]);

			radioExpand.addExpandButton(expandButton);
		}

		radioExpand.requestLayout();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		if (s.equals(context.getString(R.string.saved_max_level)))
			updateLevelList();
	}
}
