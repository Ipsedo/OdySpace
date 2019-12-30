package com.samuelberrien.odyspace.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.core.Level;
import com.samuelberrien.odyspace.game.LevelActivity;
import com.samuelberrien.odyspace.ui.infos.BonusInfosView;
import com.samuelberrien.odyspace.ui.infos.BossKilledView;
import com.samuelberrien.odyspace.ui.infos.FireInfosView;
import com.samuelberrien.odyspace.ui.infos.ItemInfosView;
import com.samuelberrien.odyspace.ui.infos.ShipInfosView;
import com.samuelberrien.odyspace.ui.shop.ShopFragment;

public class MainActivity
		extends AppCompatActivity
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String LEVEL_ID = "LEVEL_ID";
	public static final int RESULT_VALUE = 1;

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;

	private ShopFragment shopFragment;
	private LevelsFragment levelsFragment;
	private SettingsFragment settingsFragment;

	private SharedPreferences savedShop;

	private ItemInfosView shipView;
	private ItemInfosView fireView;

	private BossKilledView bossKilledView;

	private Dialog resetDialog;
	private LinearLayout layoutDialog;
	private Button resetYes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_main);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawerLayout = findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
		drawerLayout.addDrawerListener(drawerToggle);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		levelsFragment = new LevelsFragment();
		shopFragment = new ShopFragment();
		settingsFragment = new SettingsFragment();

		initDialog();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.content_fragment, levelsFragment);

		transaction.commit();

		savedShop = getApplicationContext().getSharedPreferences(
				getString(R.string.shop_preferences),
				Context.MODE_PRIVATE);

		initItems();

		switchOrientation(getResources().getConfiguration().orientation);

		getSharedPreferences(getString(R.string.shop_preferences),
				Context.MODE_PRIVATE)
				.registerOnSharedPreferenceChangeListener(this);
	}

	private void initDialog() {
		resetDialog = new Dialog(this, R.style.AppTheme);
		resetDialog.getWindow()
				.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		resetDialog.setCancelable(false);
		resetDialog.setCanceledOnTouchOutside(false);

		resetYes = new Button(this);
		resetYes.setBackground(ContextCompat.getDrawable(this, R.drawable.button));
		resetYes.setText(getString(R.string.yes));

		Button resetNo = new Button(this);
		resetNo.setBackground(ContextCompat.getDrawable(this, R.drawable.button));
		resetNo.setText(getString(R.string.no));

		resetNo.setOnClickListener((view) -> resetDialog.dismiss());

		layoutDialog = new LinearLayout(this);
		layoutDialog.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		layoutDialog.setBackground(ContextCompat.getDrawable(this,
				R.drawable.grey_corner));
		layoutDialog.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.weight = 1f;

		View v = new View(this);
		v.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.black)));
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, r.getDisplayMetrics());
		v.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				(int) px));

		layoutDialog.addView(resetYes, layoutParams);
		layoutDialog.addView(v);
		layoutDialog.addView(resetNo, layoutParams);

		resetDialog.setContentView(layoutDialog);
	}

	private void initItems() {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.weight = 1f;

		fireView = new FireInfosView(this);
		((LinearLayout) findViewById(R.id.used_items)).addView(fireView, layoutParams);
		fireView.setGLViewOnTop(true);

		shipView = new ShipInfosView(this);
		((LinearLayout) findViewById(R.id.used_items)).addView(shipView, layoutParams);
		shipView.setGLViewOnTop(true);

		ItemInfosView bonusView = new BonusInfosView(this);
		((LinearLayout) findViewById(R.id.used_items)).addView(bonusView, layoutParams);
		bonusView.setGLViewOnTop(true);

		int currMoney = savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));
		TextView textView = findViewById(R.id.money_text);
		textView.setText(getString(R.string.money, currMoney));
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switchOrientation(getResources().getConfiguration().orientation);

		if (requestCode == MainActivity.RESULT_VALUE) {
			if (resultCode == Activity.RESULT_OK) {
				int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
				int currMoney = this.savedShop.getInt(
						getString(R.string.saved_money),
						defaultMoney);
				int score = data.getIntExtra(LevelActivity.LEVEL_SCORE, 0);
				SharedPreferences.Editor editor = this.savedShop.edit();
				editor.putInt(getString(R.string.saved_money), currMoney + score);
				editor.apply();
				String result = data.getStringExtra(LevelActivity.LEVEL_RESULT);
				int levelDoneIndex = data.getIntExtra(MainActivity.LEVEL_ID, -1);
				if (result.equals(LevelActivity.WIN)) {
					/* level done */
					increaseLevel(levelDoneIndex);
				} else if (result.equals(LevelActivity.FAIIL)) {
					/* level failed */
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				/* level canceled */
			}
		}

	}

	private void increaseLevel(int currentLevelIndex) {
		SharedPreferences sevedLevelInfo = getSharedPreferences(
				getString(R.string.level_info_preferences),
				Context.MODE_PRIVATE);
		int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
		int maxLevel = sevedLevelInfo.getInt(
				getString(R.string.saved_max_level),
				defaultValue);
		System.out.println("Yo " + currentLevelIndex + " " + maxLevel);
		if (currentLevelIndex == maxLevel) {
			sevedLevelInfo.edit()
					.putInt(getString(R.string.saved_max_level),
							maxLevel + 1 < Level.LEVELS.length ?
									maxLevel + 1 : Level.LEVELS.length)
					.apply();
			System.out.println("Yo level increased");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toolbar_menu, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);

		switchOrientation(newConfig.orientation);
	}

	private void switchOrientation(int orientation) {
		LinearLayout menuDrawer = findViewById(R.id.menu_drawer);
		LinearLayout layoutMenu = findViewById(R.id.layout_menu_button);
		View mainSeparator = findViewById(R.id.main_separator);
		LinearLayout layoutItem = findViewById(R.id.used_items);

		LinearLayout.LayoutParams layoutPortraitParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams layoutLandParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);

		Resources r = getResources();
		LinearLayout.LayoutParams layoutParams;

		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				menuDrawer.setOrientation(LinearLayout.HORIZONTAL);
				layoutMenu.setLayoutParams(layoutLandParams);
				layoutItem.setLayoutParams(layoutLandParams);
				layoutParams = new LinearLayout.LayoutParams(
						(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
								2,
								r.getDisplayMetrics()),
						ViewGroup.LayoutParams.MATCH_PARENT);
				mainSeparator.setLayoutParams(layoutParams);
				shipView.dismissDialog();
				fireView.dismissDialog();
				resetDialog.dismiss();
				break;
			case Configuration.ORIENTATION_PORTRAIT:
				menuDrawer.setOrientation(LinearLayout.VERTICAL);
				layoutMenu.setLayoutParams(layoutPortraitParams);
				layoutItem.setLayoutParams(layoutPortraitParams);
				layoutParams = new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
								1,
								r.getDisplayMetrics()));
				mainSeparator.setLayoutParams(layoutParams);
				shipView.dismissDialog();
				fireView.dismissDialog();
				resetDialog.dismiss();
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				drawerLayout.openDrawer(GravityCompat.START);
				return true;
			case R.id.settings_item_menu:
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.content_fragment, settingsFragment);
				transaction.addToBackStack(null);
				transaction.commit();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void continueStory(View v) {
		drawerLayout.closeDrawers();

		int maxLevel = getSharedPreferences(
				getString(R.string.level_info_preferences),
				Context.MODE_PRIVATE)
				.getInt(getString(R.string.saved_max_level),
						getResources().getInteger(R.integer.saved_max_level_default));

		/*if (maxLevel >= Level.LEVELS.length)
			maxLevel = Level.LEVELS.length - 1;*/

		Intent intent = new Intent(this, LevelActivity.class);
		intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(maxLevel));
		startActivityForResult(intent, MainActivity.RESULT_VALUE);
	}

	public void levels(View v) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.content_fragment, levelsFragment);
		transaction.addToBackStack(null);
		transaction.commit();

		drawerLayout.closeDrawers();

	}

	public void shop(View v) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.content_fragment, shopFragment);
		transaction.addToBackStack(null);
		transaction.commit();

		drawerLayout.closeDrawers();
	}

	public void resetSettings(View v) {
		showDialogConfirm(() ->
				getSharedPreferences(getString(R.string.game_preferences), Context.MODE_PRIVATE).edit()
						.remove(getString(R.string.saved_sound_effect_volume))
						.remove(getString(R.string.saved_joystick_inversed))
						.remove(getString(R.string.saved_max_level))
						.remove(getString(R.string.saved_yaw_roll_switched))
						.apply()
		);
	}

	public void resetShop(View v) {
		showDialogConfirm(() -> {
			SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shop_preferences),
					Context.MODE_PRIVATE)
					.edit();
			editor.remove(getString(R.string.saved_money));

			String[] fire = getResources().getStringArray(R.array.fire_shop_list_item);
			for (String f : fire)
				editor.remove(f);

			String[] ship = getResources().getStringArray(R.array.ship_shop_list_item);
			for (String s : ship)
				editor.remove(s);

			String[] bonus = getResources().getStringArray(R.array.bonus_shop_list_item);
			for (String b : bonus)
				editor.remove(b);

			editor.remove(getString(R.string.bought_life));
			editor.remove(getString(R.string.bought_duration));
			editor.apply();

			editor = getSharedPreferences(getString(R.string.ship_info_preferences), Context.MODE_PRIVATE)
					.edit();
			editor.remove(getString(R.string.current_bonus_used))
					.remove(getString(R.string.current_ship_used))
					.remove(getString(R.string.current_fire_type))
					.remove(getString(R.string.current_bonus_duration))
					.remove(getString(R.string.current_life_number))
					.apply();
		});
	}

	public void resetLevels(View v) {
		showDialogConfirm(() ->
				getSharedPreferences(getString(R.string.level_info_preferences), Context.MODE_PRIVATE)
						.edit()
						.remove(getString(R.string.saved_max_level))
						.apply()
		);
	}

	public void cheat(View v) {
		showDialogConfirm(() -> {
			getSharedPreferences(getString(R.string.shop_preferences), Context.MODE_PRIVATE)
					.edit()
					.putInt(getString(R.string.saved_money), 999999999)
					.apply();
			getSharedPreferences(getString(R.string.level_info_preferences), Context.MODE_PRIVATE)
					.edit()
					.putInt(getString(R.string.saved_max_level), Level.LEVELS.length - 1)
					.apply();
		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		if (s.equals(getString(R.string.saved_money))) {
			int currMoney = savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));
			TextView textView = findViewById(R.id.money_text);
			textView.setText(getString(R.string.money, currMoney));
		}
	}

	private void showDialogConfirm(final Runnable runnable) {
		resetYes.setOnClickListener((view) -> {
			runnable.run();
			resetDialog.dismiss();
		});
		Point screenSize = getScreenSize();
		resetDialog.getWindow().setLayout(screenSize.x * 3 / 4, screenSize.y / 3);
		layoutDialog.requestLayout();
		resetDialog.show();
	}

	private Point getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START))
			drawerLayout.closeDrawer(GravityCompat.START);
		else super.onBackPressed();
	}
}
