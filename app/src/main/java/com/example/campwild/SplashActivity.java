package com.example.campwild;
import com.example.campwild.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView textTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progressBar);
        textTitle = findViewById(R.id.textTitle);

        // Optional: Add animation to the title
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textTitle.startAnimation(fadeIn);

        // Simulate a loading process and update the progress bar
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
                        Thread.sleep(50); // Simulate a delay
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress++;
                }

                // Start the main activity after the loading is complete
                Intent intent = new Intent(SplashActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }
}


