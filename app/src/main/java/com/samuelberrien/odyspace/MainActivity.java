package com.samuelberrien.odyspace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.samuelberrien.odyspace.game.LevelActivity;
import com.samuelberrien.odyspace.shop.ShopActivity;
import com.samuelberrien.odyspace.utils.game.Level;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_VALUE = 1;
    public static final String LEVEL_ID = "LEVEL_ID";

    private int currLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currLevel = 0;
        setContentView(R.layout.activity_main);

        //this.resetSharedPref();
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
        Intent intent = new Intent(this, LevelActivity.class);
        intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(0));
        startActivityForResult(intent, MainActivity.RESULT_VALUE);
    }

    public void continueStory(View v) {
        Intent intent = new Intent(this, LevelActivity.class);
        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.level_info), Context.MODE_PRIVATE);
        int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
        long maxLevel = sharedPref.getInt(getString(R.string.saved_max_level), defaultValue);
        intent.putExtra(MainActivity.LEVEL_ID, Integer.toString((int) maxLevel));
        startActivityForResult(intent, MainActivity.RESULT_VALUE);
    }

    public void shop(View v) {
        Intent intent = new Intent(this, ShopActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.RESULT_VALUE: {
                if (resultCode == Activity.RESULT_OK) {
                    int result = Integer.parseInt(data.getStringExtra(LevelActivity.LEVEL_RESULT));
                    if (result == 1) {
                        this.currLevel++;
                        if (this.currLevel > Level.MAX_LEVEL) {
                            this.currLevel--;
                        }
                        SharedPreferences sharedPref = this.getApplicationContext().getSharedPreferences(getString(R.string.level_info), Context.MODE_PRIVATE);
                        int defaultValue = getResources().getInteger(R.integer.saved_max_level_default);
                        long maxLevel = sharedPref.getInt(getString(R.string.saved_max_level), defaultValue);
                        if (this.currLevel > maxLevel) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putInt(getString(R.string.saved_max_level), this.currLevel);
                            editor.commit();
                        }
                    }
                    int score = Integer.parseInt(data.getStringExtra(LevelActivity.LEVEL_SCORE));
                }
                break;
            }
        }
    }
}
