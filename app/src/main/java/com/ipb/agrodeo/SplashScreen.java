package com.ipb.agrodeo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SplashScreen extends AppCompatActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein);
        anim.reset();

        Animation anims = AnimationUtils.loadAnimation(this, R.anim.translate);
        anims.reset();

        // Put Here RelativeLayout to original settings //
        //anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        //anim.reset();

        /*
        TextView iv = (TextView) findViewById(R.id.textView);
        iv.clearAnimation();
        iv.startAnimation(anim);
        */

        ConstraintLayout l = findViewById(R.id.activity_splash_screen);
        l.clearAnimation();
        l.startAnimation(anim);

        LinearLayout ll = findViewById(R.id.linearLayoutSplash);
        ll.clearAnimation();
        ll.startAnimation(anim);
        ll.startAnimation(anims);


        /* Called when the activity is first created. */
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 4000) {
                        sleep(100);
                        waited += 100;
                    }
                    checkFirstRun();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    com.ipb.agrodeo.SplashScreen.this.finish();
                }

            }
        };
        splashTread.start();

    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // TODO This is just a normal run
            intent = new Intent(com.ipb.agrodeo.SplashScreen.this,
                    LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO This is a new install (or the user cleared the shared preferences)
            intent = new Intent(com.ipb.agrodeo.SplashScreen.this,
                    FirstOpenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
            intent = new Intent(com.ipb.agrodeo.SplashScreen.this,
                    LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }

        startActivity(intent);

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}