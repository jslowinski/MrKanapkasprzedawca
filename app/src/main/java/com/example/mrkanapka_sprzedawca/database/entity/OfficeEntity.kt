package com.example.mrkanapka_sprzedawca.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "offices")

data class OfficeEntity(

    @ColumnInfo(name = "id_office")
    var id_seller: Int,

    @ColumnInfo(name = "sellername")
    var sellername: String,

    @ColumnInfo(name = "id_destination")
    var id_destination: Int

)
{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}