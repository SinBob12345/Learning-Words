package com.example.dailyword

import android.app.Application
import com.example.dailyword.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DailyWordApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DailyWordApplication)
            modules(appModules)
        }
    }
}
