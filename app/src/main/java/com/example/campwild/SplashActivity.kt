package com.example.campwild;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        TextView textTitle = findViewById(R.id.textTitle);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textTitle.startAnimation(fadeIn);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.flicker_animation);
        textTitle.startAnimation(animation);
        simulateLoading();
    }

    private void simulateLoading() {
        final int totalProgress = 100;
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = 0;
                while (progress <= totalProgress) {
                    final int finalProgress = progress;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(finalProgress);
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress++;
                }

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    startActivity(new Intent(SplashActivity.this, MapsActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        }).start();
    }
}


