package com.ipb.agrodeo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class FirstOpenActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_open);

        // Set OnClickListener
        findViewById(R.id.btnProceed).setOnClickListener(this);

        // Start the animations
        StartAnimations();
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein);
        anim.reset();

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();

        // Put Here RelativeLayout to original settings //
        //anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        //anim.reset();

        /*
        TextView iv = (TextView) findViewById(R.id.textView);
        iv.clearAnimation();
        iv.startAnimation(anim);
        */

        AppCompatTextView tv_first = findViewById(R.id.tvFirst);
        Button btn_proceed = findViewById(R.id.btnProceed);

        tv_first.clearAnimation();
        tv_first.startAnimation(anim);
        btn_proceed.clearAnimation();
        btn_proceed.startAnimation(anim);

    }

    @Override
    public void onClick(View v) {
        Intent inten = new Intent(this, LoginActivity.class);
        startActivity(inten);
        finish();
    }
}