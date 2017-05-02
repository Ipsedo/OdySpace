package com.samuelberrien.odyspace;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_VALUE = 1;
    public static final String LEVEL_ID = "LEVEL_ID";

    private int currLevel;

    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.currLevel = 0;
        setContentView(R.layout.activity_main);
    }

    public void start(View v){
        Intent intent = new Intent(this, LevelActivity.class);
        intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(0));
        startActivityForResult(intent, MainActivity.RESULT_VALUE);
    }

    public void next(View v){
        Intent intent = new Intent(this, LevelActivity.class);
        intent.putExtra(MainActivity.LEVEL_ID, Integer.toString(this.currLevel));
        startActivityForResult(intent, MainActivity.RESULT_VALUE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (MainActivity.RESULT_VALUE) : {
                if (resultCode == Activity.RESULT_OK) {
                    int result = Integer.parseInt(data.getStringExtra(LevelActivity.RESULT));
                    if(result == 1) {
                        this.currLevel++;
                        if(this.currLevel > MyGLRenderer.LEVEL_MAX) {
                            this.currLevel--;
                        }
                    }
                }
                break;
            }
        }
    }
}
