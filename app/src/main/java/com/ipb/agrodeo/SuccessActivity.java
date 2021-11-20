package com.ipb.agrodeo;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import cdflynn.android.library.checkview.CheckView;

public class SuccessActivity extends AppCompatActivity {
    private TextView tvSuccess;
    private CheckView mCheckview;
    private Intent inten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        tvSuccess = findViewById(R.id.txtSuccess);
//        mCheckview = findViewById(R.id.check);
//        mCheckview.check();

        // Get the previous FRAGMENT_ID code
        int fragmentId = getIntent().getIntExtra("FRAGMENT_ID", 100);

        // Start animation(s)
        startAnimations();

        /* Called when the activity is first created. */
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 2000) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    inten = new Intent(com.ipb.agrodeo.SuccessActivity.this, HomeNavigation.class);
                    if(fragmentId == 0){
                        inten.putExtra("FRAGMENT_ID_NAV", 0);
                    } else if(fragmentId == 1){
                        inten.putExtra("FRAGMENT_ID_NAV", 1);
                    }
                    startActivity(inten);
                    com.ipb.agrodeo.SuccessActivity.this.finish();
                }
            }
        };
        splashThread.start();
    }

    private void startAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein_success);
        anim.reset();
        tvSuccess.clearAnimation();
        tvSuccess.startAnimation(anim);

        // Put Here RelativeLayout to original settings //
        //anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        //anim.reset();

        /*
        TextView iv = (TextView) findViewById(R.id.textView);
        iv.clearAnimation();
        iv.startAnimation(anim);
        */
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}