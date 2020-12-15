package com.magic.smarttravel

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.magic.smarttravel.data.repositoryModule
import com.magic.smarttravel.screens.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by Marta Turchyniak on 12/2/20.
 */
class SmartTravelApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SmartTravelApplication)
            modules(
                arrayListOf(
                    viewModelModule,
                    repositoryModule
                )
            )
        }

        // Initialize the SDK
        Places.initialize(applicationContext, MAP_KEY)
    }

    companion object {
        val MAP_KEY = "AIzaSyBVP6oezLvnDWB_ycMGHPDe-hzUf_FntKg"
    }
}