package com.samuelberrien.odyspace.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.game.LevelActivity;
import com.samuelberrien.odyspace.shop.ShopActivity;
import com.samuelberrien.odyspace.utils.game.Level;
import com.samuelberrien.odyspace.utils.main.ItemImageViewMaker;
import com.samuelberrien.odyspace.utils.main.ViewHelper;

public class MainActivity extends AppCompatActivity {

	private static final int RESULT_VALUE = 1;
	public static final String LEVEL_ID = "LEVEL_ID";

	private int currLevel;

	private Button startButton;
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
		this.startButton = (Button) findViewById(R.id.start_button);
		this.startButton.setText("START (" + (this.currLevel + 1) + ")");
		this.continueButton = (Button) findViewById(R.id.continue_button);
		this.shopButton = (Button) findViewById(R.id.shop_button);
		this.myToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
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

		for (int i = 0; i < maxLevel; i++) {
			final int currLvl = i;
			final Button levelItem = new Button(this);
			levelItem.setAllCaps(false);
			levelItem.setText((i + 1) + " - " + Level.LEVELS[i]);
			levelItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ViewHelper.makeViewTransition(MainActivity.this, levelItem);
					MainActivity.this.currLevel = currLvl;
					MainActivity.this.startButton.setText("START (" + (MainActivity.this.currLevel + 1) + ")");
				}
			});
			levelItem.setClickable(true);
			levelItem.setBackgroundResource(R.drawable.transition_button_main);
			levelItem.setLayoutParams(params);
			levelChooser.addView(levelItem);
		}
	}

	private void initGameInfo() {
		final String currFireType = this.savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default));

		final int currShipLife = this.savedShip.getInt(getString(R.string.current_life_number), getResources().getInteger(R.integer.saved_ship_life_default));

		final int currBoughtLife = this.savedShop.getInt(getString(R.string.bought_life), getResources().getInteger(R.integer.saved_ship_life_shop_default));

		int currMoney = this.savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));

		final String shipUsed = this.savedShip.getString(getString(R.string.current_ship_used), getString(R.string.saved_ship_used_default));


		ImageView imageView = (ImageView) findViewById(R.id.fire_image_main);
		ItemImageViewMaker.makeFireTypeImage(this, this.myToast, imageView, currFireType);

		imageView = (ImageView) findViewById(R.id.ship_image_main);
		ItemImageViewMaker.makeShipImage(this, this.myToast, imageView, shipUsed, currShipLife, currBoughtLife);


		TextView textView = (TextView) findViewById(R.id.curr_money_main);
		textView.setText(Integer.toString(currMoney).concat(" $"));
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


	public void start(View v) {
		ViewHelper.makeViewTransition(this, this.startButton);
		Intent intent = new Intent(this, LevelActivity.class);
		intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(this.currLevel));
		startActivityForResult(intent, MainActivity.RESULT_VALUE);
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
						MainActivity.this.startButton.setText("START (" + (MainActivity.this.currLevel + 1) + ")");
						if (MainActivity.this.currLevel > maxLevel) {
							SharedPreferences.Editor editorLevel = MainActivity.this.savedLevelInfo.edit();
							editorLevel.putInt(getString(R.string.saved_max_level), MainActivity.this.currLevel);
							editorLevel.apply();
						}
						MainActivity.this.initLevelChooser();

						builder = builder.setTitle("Level Done")
								.setMessage("Score : " + score)
								.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {

									}
								})
								.setPositiveButton("Next", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
										MainActivity.this.startButton.setText("START (" + (MainActivity.this.currLevel + 1) + ")");
										Intent intent = new Intent(MainActivity.this, LevelActivity.class);
										intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(MainActivity.this.currLevel));
										startActivityForResult(intent, MainActivity.RESULT_VALUE);
									}
								});
					} else {
						builder = builder.setTitle("Game Over")
								.setMessage("Score : " + score)
								.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {

									}
								})
								.setNegativeButton("Restart", new DialogInterface.OnClickListener() {
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
	}
}
