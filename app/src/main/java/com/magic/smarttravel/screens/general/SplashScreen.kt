package com.magic.smarttravel.screens.general

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.auth.SignInActivity

/**
 * Created by Marta Turchyniak on 5/24/20.
 */
class SplashScreen : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 3000L

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            SignInActivity.start(this)
        }, SPLASH_DISPLAY_LENGTH)
    }

    override fun onStop() {
        handler.removeCallbacksAndMessages(null)
        super.onStop()
    }
}