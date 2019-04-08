package com.example.mrkanapka_sprzedawca.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "token")

data class TokenEntity (

    @ColumnInfo(name = "token")
    var token: String,
    @ColumnInfo(name = "id_seller")
    var id_seller : Int
){

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}