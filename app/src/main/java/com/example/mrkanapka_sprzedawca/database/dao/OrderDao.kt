package com.example.mrkanapka_sprzedawca.database.dao

import android.arch.persistence.room.*
import io.reactivex.Maybe


@Dao
abstract class OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<OrderDao>)

    @Query("DELETE FROM orders WHERE order_number LIKE :order_number")
    abstract fun removeOrders(order_number: String)

    @Query("SELECT * FROM orders WHERE order_number LIKE :order_number")
    abstract fun getOrders(order_number: String): Maybe<List<OrderDao>>

    @Transaction
    open fun removeAndInsert(entities: List<OrderDao>, order_number: String){
        removeOrders(order_number)
        insert(entities)
    }
}