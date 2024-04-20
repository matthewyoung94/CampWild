package com.example.campwild

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        progressBar = findViewById(R.id.progressBar)
        val textTitle = findViewById<TextView>(R.id.textTitle)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        textTitle.startAnimation(fadeIn)
        val animation = AnimationUtils.loadAnimation(this, R.anim.flicker_animation)
        textTitle.startAnimation(animation)
        simulateLoading()
    }

    private fun simulateLoading() {
        val totalProgress = 100
        val handler = Handler()
        Thread {
            var progress = 0
            while (progress <= totalProgress) {
                val finalProgress = progress
                handler.post { progressBar!!.progress = finalProgress }
                try {
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                progress++
            }
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                startActivity(Intent(this@SplashActivity, MapsActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }.start()
    }
}
