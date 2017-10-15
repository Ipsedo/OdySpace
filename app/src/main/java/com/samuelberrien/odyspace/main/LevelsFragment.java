package com.samuelberrien.odyspace.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.game.LevelActivity;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.widget.ExpandButton;
import com.samuelberrien.odyspace.utils.widget.RadioExpand;

/**
 * Created by samuel on 12/10/17.
 */

public class LevelsFragment
		extends Fragment
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	private SharedPreferences savedLevelInfo;
	//private LinearLayout levelChooser;
	private RadioExpand radioExpand;

	private LayoutInflater inflater;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		savedLevelInfo = getActivity().getApplicationContext()
				.getSharedPreferences(getString(R.string.level_info_preferences), Context.MODE_PRIVATE);

		this.inflater = inflater;

		View v = inflater.inflate(R.layout.new_levels, container, false);

		//levelChooser = (LinearLayout) v.findViewById(R.id.level_chooser_layout);
		radioExpand = (RadioExpand) v.findViewById(R.id.radio_expand_level);

		updateLevelList();

		return v;
	}

	private void updateLevelList() {
		int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
		int maxLevel = savedLevelInfo.getInt(getString(R.string.saved_max_level), defaultValue);

		radioExpand.removeAllViews();

		for (int i = 0; i < maxLevel; i++) {
			final int indexLevel = i;
			Runnable launchLevel = new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(getActivity(), LevelActivity.class);
					intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(indexLevel));
					startActivityForResult(intent, MainActivity.RESULT_VALUE);
				}
			};

			ExpandButton expandButton = new ExpandButton(getContext(), launchLevel);
			expandButton.setText((i + 1) + " - " + Level.LEVELS[i]);

			radioExpand.addExpandButton(expandButton);
		}

		radioExpand.requestLayout();

		//levelChooser.removeAllViews();

		/*LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, getResources().getDisplayMetrics());
		layoutParams.setMargins(margin, margin, margin, 0);*/

		/*final ArrayList<Button> playButtons = new ArrayList<>();
		for (int i = 0; i < maxLevel; i++) {
			final int currLvl = i;
			LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.expand_button, null);
			TextView level = (TextView) linearLayout.findViewById(R.id.expand_text);
			level.setText((i + 1) + " - " + Level.LEVELS[i]);

			final Button play = (Button) linearLayout.findViewById(R.id.expand_button);
			play.setText("▶");
			playButtons.add(play);
			level.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					currLevel = currLvl;

					for (Button b : playButtons)
						b.setVisibility(View.GONE);
					play.setVisibility(View.VISIBLE);
				}
			});
			play.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), LevelActivity.class);
					intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(currLevel));
					startActivityForResult(intent, MainActivity.RESULT_VALUE);
				}
			});
			//linearLayout.setLayoutParams(layoutParams);
			levelChooser.addView(linearLayout);
		}*/
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		if (s.equals(getString(R.string.saved_max_level))) {
			updateLevelList();
		}
	}
}
