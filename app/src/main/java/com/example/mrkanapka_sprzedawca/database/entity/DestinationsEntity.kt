package com.example.mrkanapka_sprzedawca.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "destinations")

data class DestinationsEntity(

    @ColumnInfo(name = "id_destination")
    var id_destination: Int,

    @ColumnInfo(name = "name")
    var name: String

)
{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}