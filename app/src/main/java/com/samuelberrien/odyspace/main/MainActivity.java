package com.samuelberrien.odyspace.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.samuelberrien.odyspace.R;
import com.samuelberrien.odyspace.game.LevelActivity;
import com.samuelberrien.odyspace.shop.ShopActivity;
import com.samuelberrien.odyspace.utils.game.Level;

public class MainActivity extends AppCompatActivity {

	private static final int RESULT_VALUE = 1;
	public static final String LEVEL_ID = "LEVEL_ID";

	private int currLevel;

	private Button startButton;
	private Button continueButton;
	private Button shopButton;
	private LinearLayout levelChooser;

	private SharedPreferences savedShop;
	private SharedPreferences savedLevelInfo;
	private SharedPreferences savedShip;

	private Animation myAnim;

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
		this.myAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
		this.initGameInfo();
		this.initLevelChooser();
	}

	private void initLevelChooser() {
		this.levelChooser = (LinearLayout) findViewById(R.id.level_chooser_layout);

		int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
		int maxLevel = this.savedLevelInfo.getInt(getString(R.string.saved_max_level), defaultValue);

		this.levelChooser.removeAllViews();

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);
		params.setMargins(0, 10, 0, 0);

		for (int i = 0; i < maxLevel; i++) {
			final int currLvl = i;
			Button levelItem = new Button(this);
			levelItem.setAllCaps(false);
			levelItem.setText((i + 1) + " - " + Level.LEVELS[i]);
			levelItem.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					MainActivity.this.currLevel = currLvl;
					MainActivity.this.startButton.setText("START (" + (MainActivity.this.currLevel + 1) + ")");
				}
			});
			levelItem.setClickable(true);
			levelItem.setBackgroundResource(R.drawable.button_main);
			levelItem.setLayoutParams(params);
			this.levelChooser.addView(levelItem);
		}
	}

	private void initGameInfo() {
		String currFireType = this.savedShip.getString(getString(R.string.current_fire_type), getString(R.string.saved_fire_type_default));

		int currShipLife = this.savedShip.getInt(getString(R.string.current_life_number), getResources().getInteger(R.integer.saved_ship_life_default));

		int currBoughtLife = this.savedShop.getInt(getString(R.string.bought_life), getResources().getInteger(R.integer.saved_ship_life_shop_default));

		int currMoney = this.savedShop.getInt(getString(R.string.saved_money), getResources().getInteger(R.integer.saved_init_money));

		String shipUsed = this.savedShip.getString(getString(R.string.current_ship_used), getString(R.string.saved_ship_used_default));


		ImageView imageView = (ImageView) findViewById(R.id.fire_image_main);
		if (currFireType.equals(getString(R.string.fire_bonus_1))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.simple_fire));
		} else if (currFireType.equals(getString(R.string.fire_bonus_2))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.quint_fire));
		} else if (currFireType.equals(getString(R.string.fire_bonus_3))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.simple_bomb));
		}

		imageView = (ImageView) findViewById(R.id.ship_image_main);

		if (shipUsed.equals(getString(R.string.ship_simple))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.simple_ship));
		} else if (shipUsed.equals(getString(R.string.ship_bird))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ship_bird));
		} else if (shipUsed.equals(getString(R.string.ship_supreme))) {
			imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ship_supreme));
		}

		TextView textView = (TextView) findViewById(R.id.curr_money_main);
		textView.setText("Life : " + currShipLife + " + " + currBoughtLife + System.getProperty("line.separator") + currMoney + " $");
	}

	private void resetSharedPref() {
		SharedPreferences.Editor editor = this.savedShop.edit();
		editor.clear().commit();
		editor = this.savedShip.edit();
		editor.clear().commit();
		editor = this.savedLevelInfo.edit();
		editor.clear().commit();
	}

	public void reset(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Reset all game ?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				MainActivity.this.resetSharedPref();
				MainActivity.this.initGameInfo();
				MainActivity.this.initLevelChooser();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

			}
		});
		AlertDialog dialog = builder.create();
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.button_main);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public void start(View v) {
		this.startButton.startAnimation(this.myAnim);
		Intent intent = new Intent(this, LevelActivity.class);
		intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(this.currLevel));
		startActivityForResult(intent, MainActivity.RESULT_VALUE);
	}

	public void continueStory(View v) {
		this.continueButton.startAnimation(this.myAnim);
		Intent intent = new Intent(this, LevelActivity.class);
		int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
		long maxLevel = this.savedLevelInfo.getInt(getString(R.string.saved_max_level), defaultValue);
		this.currLevel = (int) maxLevel;
		intent.putExtra(MainActivity.LEVEL_ID, Integer.toString((int) maxLevel));
		startActivityForResult(intent, MainActivity.RESULT_VALUE);
	}

	public void shop(View v) {
		this.shopButton.startAnimation(this.myAnim);
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
					editor.commit();

					int result = Integer.parseInt(data.getStringExtra(LevelActivity.LEVEL_RESULT));
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					if (result == 1) {
						int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
						final long maxLevel = MainActivity.this.savedLevelInfo.getInt(getString(R.string.saved_max_level), defaultValue);

						builder.setTitle("Level Done");
						builder.setMessage("Score : " + score);

						builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (MainActivity.this.currLevel < Level.LEVELS.length)
									MainActivity.this.currLevel++;
								MainActivity.this.startButton.setText("START (" + (MainActivity.this.currLevel + 1) + ")");
								if (MainActivity.this.currLevel > maxLevel) {
									SharedPreferences.Editor editorLevel = MainActivity.this.savedLevelInfo.edit();
									editorLevel.putInt(getString(R.string.saved_max_level), MainActivity.this.currLevel);
									editorLevel.commit();
								}
								MainActivity.this.initLevelChooser();
							}
						});

						builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								if (MainActivity.this.currLevel < Level.LEVELS.length)
									MainActivity.this.currLevel++;
								if (MainActivity.this.currLevel > maxLevel) {
									SharedPreferences.Editor editorLevel = MainActivity.this.savedLevelInfo.edit();
									editorLevel.putInt(getString(R.string.saved_max_level), MainActivity.this.currLevel);
									editorLevel.commit();
								}
								MainActivity.this.initLevelChooser();
								MainActivity.this.startButton.setText("START (" + (MainActivity.this.currLevel + 1) + ")");
								Intent intent = new Intent(MainActivity.this, LevelActivity.class);
								intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(MainActivity.this.currLevel));
								startActivityForResult(intent, MainActivity.RESULT_VALUE);

							}
						});
					} else {
						builder.setTitle("Game Over");
						builder.setMessage("Score : " + score);

						builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

							}
						});

						builder.setNegativeButton("Restart", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								Intent intent = new Intent(MainActivity.this, LevelActivity.class);
								intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(MainActivity.this.currLevel));
								startActivityForResult(intent, MainActivity.RESULT_VALUE);
							}
						});
					}
					AlertDialog dialog = builder.create();
					dialog.getWindow().setBackgroundDrawableResource(R.drawable.button_main);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}
				break;
			}
		}
		this.initGameInfo();
	}
}
