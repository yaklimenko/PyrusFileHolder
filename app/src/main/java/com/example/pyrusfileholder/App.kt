package com.example.pyrusfileholder

import android.app.Application
import com.example.pyrusfileholder.di.AppComponent
import com.example.pyrusfileholder.di.AppModule
import com.example.pyrusfileholder.di.DaggerAppComponent

class App: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        initAppComponent()
        super.onCreate()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}