package com.samuelberrien.odyspace;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.opengl.Matrix;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
    private TextView gameInfo;
    private LinearLayout levelChooser;

    private Animation myAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currLevel = 0;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        //this.resetSharedPref();
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

        SharedPreferences sharedPrefLevel = this.getApplicationContext().getSharedPreferences(getString(R.string.level_info), Context.MODE_PRIVATE);
        int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
        int maxLevel = sharedPrefLevel.getInt(getString(R.string.saved_max_level), defaultValue);

        this.levelChooser.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 0, 0);

        for (int i = 0; i < maxLevel; i++) {
            final int currLvl = i;
            Button levelItem = new Button(this);
            levelItem.setText("Level " + (i + 1));
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
        this.gameInfo = (TextView) findViewById(R.id.game_info);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.saved_ship_info), Context.MODE_PRIVATE);
        String defaultValue = getString(R.string.saved_fire_type_default);
        String currFireType = sharedPref.getString(getString(R.string.current_fire_type), defaultValue);

        sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_shop), Context.MODE_PRIVATE);
        int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
        int currMoney = sharedPref.getInt(getString(R.string.saved_money), defaultMoney);

        this.gameInfo.setText("FireType : " + currFireType + System.getProperty("line.separator") + "Money : " + currMoney);
    }

    public void resetSharedPref() {
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_shop), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear().commit();
        sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_ship_info), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear().commit();
        sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.level_info), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear().commit();
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
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.level_info), Context.MODE_PRIVATE);
        int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
        long maxLevel = sharedPref.getInt(getString(R.string.saved_max_level), defaultValue);
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
                    SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.saved_shop), Context.MODE_PRIVATE);
                    int defaultMoney = getResources().getInteger(R.integer.saved_init_money);
                    int currMoney = sharedPref.getInt(getString(R.string.saved_money), defaultMoney);
                    int score = Integer.parseInt(data.getStringExtra(LevelActivity.LEVEL_SCORE));
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.saved_money), currMoney + score);
                    editor.commit();

                    int result = Integer.parseInt(data.getStringExtra(LevelActivity.LEVEL_RESULT));
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    if (result == 1) {
                        final SharedPreferences sharedPrefLevel = this.getApplicationContext().getSharedPreferences(getString(R.string.level_info), Context.MODE_PRIVATE);
                        int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
                        final long maxLevel = sharedPrefLevel.getInt(getString(R.string.saved_max_level), defaultValue);

                        builder.setTitle("Level Done, Score : " + score);

                        builder.setNegativeButton("Restart", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(MainActivity.this, LevelActivity.class);
                                intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(MainActivity.this.currLevel));
                                startActivityForResult(intent, MainActivity.RESULT_VALUE);
                            }
                        });

                        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (MainActivity.this.currLevel < Level.MAX_LEVEL)
                                    MainActivity.this.currLevel++;
                                MainActivity.this.startButton.setText("START (" + (MainActivity.this.currLevel + 1) + ")");
                                if (MainActivity.this.currLevel > maxLevel) {
                                    SharedPreferences.Editor editorLevel = sharedPrefLevel.edit();
                                    editorLevel.putInt(getString(R.string.saved_max_level), MainActivity.this.currLevel);
                                    editorLevel.commit();
                                }
                                MainActivity.this.initLevelChooser();
                            }
                        });

                        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (MainActivity.this.currLevel < Level.MAX_LEVEL)
                                    MainActivity.this.currLevel++;
                                if (MainActivity.this.currLevel > maxLevel) {
                                    SharedPreferences.Editor editorLevel = sharedPrefLevel.edit();
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
                        builder.setTitle("Game Over, Score : " + score);

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
