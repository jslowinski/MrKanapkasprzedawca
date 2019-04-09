package com.example.mrkanapka_sprzedawca.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "orders")

data class OrderEntity(

    @ColumnInfo(name = "email")
    var email: String,

    @ColumnInfo(name = "order_number")
    var order_number: String,

    @ColumnInfo(name = "status")
    var status: String,

    @ColumnInfo(name = "registrationid")
    var registrationid: String,

    @ColumnInfo(name = "data")
    var data: String,

    @ColumnInfo(name = "id_destination")
    var id_destination: Int

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}