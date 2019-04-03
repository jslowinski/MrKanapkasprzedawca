package com.example.mrkanapka_sprzedawca.application

import android.app.Application
import com.example.mrkanapka_sprzedawca.database.AndroidDatabase
import com.facebook.stetho.Stetho

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        //Initalize Stetho
        Stetho.initializeWithDefaults(this)

        //Initalize Database
        AndroidDatabase.initialize(this)
    }
}