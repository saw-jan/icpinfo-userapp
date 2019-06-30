package com.jan.saw.icp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Flash extends AppCompatActivity {
    private static int time_out = 4000;
    private ImageView logo;
    private ImageView log;
    public static final String PREFS = "prefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        final SharedPreferences prefs = getSharedPreferences(PREFS,0);
        logo = findViewById(R.id.logo);
        log = findViewById(R.id.log);
        Animation fade_in = AnimationUtils.loadAnimation(this,R.anim.fadein);
        logo.startAnimation(fade_in);
        log.startAnimation(fade_in);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                    Intent loginIntent = new Intent(Flash.this, Login.class);
                    startActivity(loginIntent);
                    finish();
            }
        },time_out);
    }
}
