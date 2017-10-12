package com.samuelberrien.odyspace.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.main.shop.ShopFragment;
import com.samuelberrien.odyspace.utils.main.ItemInfosBuilder;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

	public static final String LEVEL_ID = "LEVEL_ID";
	public static final int RESULT_VALUE = 1;

	private Toolbar toolbar;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;

	private ShopFragment shopFragment;
	private LevelsFragment levelsFragment;
	private SettingsFragment settingsFragment;

	private SharedPreferences savedShip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_main);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
		drawerLayout.addDrawerListener(drawerToggle);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		levelsFragment = new LevelsFragment();
		shopFragment = new ShopFragment();
		settingsFragment = new SettingsFragment();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.content_fragment, levelsFragment);

		transaction.commit();

		SharedPreferences savedShop = getApplicationContext().getSharedPreferences(getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		savedShop.registerOnSharedPreferenceChangeListener(this);
		savedShip = getApplicationContext().getSharedPreferences(getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);
		savedShip.registerOnSharedPreferenceChangeListener(this);

		initItems();
	}

	public void initItems() {
		((LinearLayout) findViewById(R.id.used_items)).removeAllViews();

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.weight = 1f;

		String fireType = savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default));
		((LinearLayout) findViewById(R.id.used_items)).addView(ItemInfosBuilder.makeFireInfos(this, fireType), layoutParams);

		String shipUsed = savedShip.getString(getString(R.string.current_ship_used), getString(R.string.saved_ship_used_default));
		((LinearLayout) findViewById(R.id.used_items)).addView(ItemInfosBuilder.makeShipInfos(this, shipUsed), layoutParams);

		((LinearLayout) findViewById(R.id.used_items)).addView(new View(this), layoutParams);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
		//TODO orientation switch drawer
	}

	private void switchOrientation(int orientation) {
		LinearLayout menuDrawer = (LinearLayout) findViewById(R.id.menu_drawer);
		LinearLayout layoutMenu = (LinearLayout) findViewById(R.id.layout_menu_button);
		LinearLayout layoutItem = (LinearLayout) findViewById(R.id.used_items);

		LinearLayout.LayoutParams layoutPortraitParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams layoutLandParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);

		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				menuDrawer.setOrientation(LinearLayout.HORIZONTAL);
				layoutMenu.setLayoutParams(layoutLandParams);
				layoutItem.setLayoutParams(layoutLandParams);

				if(ItemInfosBuilder.dialog != null){
					ItemInfosBuilder.dialog.dismiss();
				}
				break;
			case Configuration.ORIENTATION_PORTRAIT:
				menuDrawer.setOrientation(LinearLayout.VERTICAL);
				layoutMenu.setLayoutParams(layoutPortraitParams);
				layoutItem.setLayoutParams(layoutPortraitParams);

				if(ItemInfosBuilder.dialog != null){
					ItemInfosBuilder.dialog.dismiss();
				}
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
				transaction.commit();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void continueStory(View v) {

	}

	public void levels(View v) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.content_fragment, levelsFragment);

		transaction.commit();

	}

	public void shop(View v) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.content_fragment, shopFragment);

		transaction.commit();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		initItems();
	}

	/*private static final int RESULT_VALUE = 1;
	public static final String LEVEL_ID = "LEVEL_ID";

	private int currLevel;

	private Button continueButton;
	private Button shopButton;

	private SharedPreferences savedShop;
	private SharedPreferences savedLevelInfo;
	private SharedPreferences savedShip;

	private Toast myToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.currLevel = 0;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		this.savedShop = this.getApplicationContext().getSharedPreferences(getString(R.string.shop_preferences), Context.MODE_PRIVATE);
		this.savedLevelInfo = this.getApplicationContext().getSharedPreferences(getString(R.string.level_info_preferences), Context.MODE_PRIVATE);
		this.savedShip = this.getApplicationContext().getSharedPreferences(getString(R.string.ship_info_preferences), Context.MODE_PRIVATE);
		this.continueButton = (Button) findViewById(R.id.continue_button);
		this.shopButton = (Button) findViewById(R.id.shop_button);
		this.myToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
		FireType.setNames(this);
		this.initGameInfo();
		this.initLevelChooser();
	}

	private int getScreenWidth() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}

	private void initLevelChooser() {
		LinearLayout levelChooser = (LinearLayout) findViewById(R.id.level_chooser_layout);

		int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
		int maxLevel = this.savedLevelInfo.getInt(getString(R.string.saved_max_level), defaultValue);

		levelChooser.removeAllViews();

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);
		params.setMargins(0, this.getScreenWidth() / 100, 0, 0);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
		layoutParams.setMargins(margin, margin, margin, 0);

		final ArrayList<Button> playButtons = new ArrayList<>();
		for (int i = 0; i < maxLevel; i++) {
			final int currLvl = i;
			LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.expand_button, null);
			TextView level = (TextView) linearLayout.findViewById(R.id.expand_text);
			level.setText((i + 1) + " - " + Level.LEVELS[i]);

			final Button play = (Button) linearLayout.findViewById(R.id.expand_button);
			play.setText("▶");
			playButtons.add(play);
			level.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MainActivity.this.currLevel = currLvl;

					for (Button b : playButtons)
						b.setVisibility(View.GONE);
					play.setVisibility(View.VISIBLE);
				}
			});
			play.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, LevelActivity.class);
					intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(currLevel));
					startActivityForResult(intent, MainActivity.RESULT_VALUE);
				}
			});
			linearLayout.setLayoutParams(layoutParams);
			levelChooser.addView(linearLayout);
		}
	}

	private void initGameInfo() {
		final String currFireType = this.savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default));

		final int currShipLife = this.savedShip.getInt(getString(R.string.current_life_number), getResources().getInteger(R.integer.saved_ship_life_default));

		final int currBoughtLife = this.savedShop.getInt(getString(R.string.bought_life), getResources().getInteger(R.integer.saved_ship_life_shop_default));

		int currMoney = this.savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));

		final String shipUsed = this.savedShip.getString(getString(R.string.current_ship_used), getString(R.string.saved_ship_used_default));

		final String bonusUsed = this.savedShip.getString(getString(R.string.current_bonus_used), getString(R.string.bonus_1));
		final int currBonusDuration = this.savedShip.getInt(getString(R.string.current_bonus_duration), getResources().getInteger(R.integer.bonus_1_duration));
		final int currBoughtDuration = this.savedShop.getInt(getString(R.string.bought_duration), 0);


		final ImageView imageView1 = (ImageView) findViewById(R.id.fire_image_main);
		ItemImageViewMaker.makeFireInfos(this, this.myToast, imageView1, currFireType);
		imageView1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewHelper.makeViewTransition(MainActivity.this, imageView1);
				showDialog(ItemImageViewMaker.makeSelectItemView(MainActivity.this, Purchases.FIRE));
			}
		});

		final ImageView imageView2 = (ImageView) findViewById(R.id.ship_image_main);
		ItemImageViewMaker.makeShipImage(this, this.myToast, imageView2, shipUsed, currShipLife, currBoughtLife);
		imageView2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewHelper.makeViewTransition(MainActivity.this, imageView2);
				showDialog(ItemImageViewMaker.makeSelectItemView(MainActivity.this, Purchases.SHIP));
			}
		});

		final ImageView imageView3 = (ImageView) findViewById(R.id.bonus_image_main);
		ItemImageViewMaker.makeBonusImage(this, this.myToast, imageView3, bonusUsed, currBonusDuration, currBoughtDuration);
		imageView3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewHelper.makeViewTransition(MainActivity.this, imageView3);
				showDialog(ItemImageViewMaker.makeSelectItemView(MainActivity.this, Purchases.BONUS));
			}
		});

		TextView textView = (TextView) findViewById(R.id.curr_money_main);
		textView.setText(Integer.toString(currMoney).concat(" $"));
	}

	private void showDialog(View v) {
		AlertDialog pauseDialog = new AlertDialog.Builder(this)
				.setTitle("Item chooser")
				.setView(v)
				.setPositiveButton(getString(R.string.check), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
					}
				})
				.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialogInterface) {
						initGameInfo();
					}
				})
				.create();
		pauseDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.button_main));
		pauseDialog.show();
	}

	private void resetSharedPref() {
		this.savedShop.edit()
				.clear()
				.apply();
		this.savedShip.edit()
				.clear()
				.apply();
		this.savedLevelInfo.edit()
				.clear()
				.apply();
		this.getSharedPreferences(getString(R.string.game_preferences), Context.MODE_PRIVATE).edit()
				.clear()
				.apply();
	}

	public void reset(View v) {
		ViewHelper.makeViewTransition(this, findViewById(R.id.reset_button));
		AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle("Reset all game ?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						MainActivity.this.resetSharedPref();
						MainActivity.this.initGameInfo();
						MainActivity.this.initLevelChooser();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				})
				.create();
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.button_main);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void continueStory(View v) {
		ViewHelper.makeViewTransition(this, this.continueButton);
		Intent intent = new Intent(this, LevelActivity.class);
		int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
		long maxLevel = this.savedLevelInfo.getInt(getString(R.string.saved_max_level), defaultValue);
		this.currLevel = (int) maxLevel;
		intent.putExtra(MainActivity.LEVEL_ID, Integer.toString((int) maxLevel));
		startActivityForResult(intent, MainActivity.RESULT_VALUE);
	}

	public void shop(View v) {
		ViewHelper.makeViewTransition(this, this.shopButton);
		Intent intent = new Intent(this, ShopActivity.class);
		startActivity(intent);
	}

	public void onResume() {
		super.onResume();
		this.initGameInfo();
		this.initLevelChooser();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case MainActivity.RESULT_VALUE: {
				if (resultCode == Activity.RESULT_OK) {
					int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
					int currMoney = this.savedShop.getInt(getString(R.string.saved_money), defaultMoney);
					int score = Integer.parseInt(data.getStringExtra(LevelActivity.LEVEL_SCORE));
					SharedPreferences.Editor editor = this.savedShop.edit();
					editor.putInt(getString(R.string.saved_money), currMoney + score);
					editor.apply();

					int result = Integer.parseInt(data.getStringExtra(LevelActivity.LEVEL_RESULT));
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					if (result == 1) {

						int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
						final long maxLevel = MainActivity.this.savedLevelInfo.getInt(getString(R.string.saved_max_level), defaultValue);
						if (MainActivity.this.currLevel < Level.LEVELS.length)
							MainActivity.this.currLevel++;
						if (MainActivity.this.currLevel > maxLevel) {
							SharedPreferences.Editor editorLevel = MainActivity.this.savedLevelInfo.edit();
							editorLevel.putInt(getString(R.string.saved_max_level), MainActivity.this.currLevel);
							editorLevel.apply();
						}
						MainActivity.this.initLevelChooser();

						builder = builder.setTitle("Level Done")
								.setMessage("Score : " + score)
								.setNegativeButton(getString(R.string.check), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {

									}
								})
								.setPositiveButton(getString(R.string.next), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
										Intent intent = new Intent(MainActivity.this, LevelActivity.class);
										intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(MainActivity.this.currLevel));
										startActivityForResult(intent, MainActivity.RESULT_VALUE);
									}
								});
					} else {
						builder = builder.setTitle("Game Over")
								.setMessage("Score : " + score)
								.setNegativeButton(getString(R.string.check), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {

									}
								})
								.setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
										Intent intent = new Intent(MainActivity.this, LevelActivity.class);
										intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(MainActivity.this.currLevel));
										startActivityForResult(intent, MainActivity.RESULT_VALUE);
									}
								});
					}
					AlertDialog alertDialog = builder.create();
					alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.button_main);
					alertDialog.setCanceledOnTouchOutside(false);
					alertDialog.show();
				}
				break;
			}
		}
		this.initGameInfo();
	}*/
}
