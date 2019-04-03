package com.example.mrkanapka_sprzedawca.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(
    version = 1,
    exportSchema = false,
    entities = []
)

abstract class AndroidDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "mrkanapkaseller_db"

        lateinit var database: AndroidDatabase
            private set

        fun initialize(context: Context){
            database = Room
                .databaseBuilder(context, AndroidDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}