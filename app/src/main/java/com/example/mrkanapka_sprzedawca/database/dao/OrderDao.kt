package com.example.mrkanapka_sprzedawca.database.dao

import android.arch.persistence.room.*
import com.example.mrkanapka_sprzedawca.api.model.OrderDto
import com.example.mrkanapka_sprzedawca.database.entity.OrderEntity
import io.reactivex.Maybe


@Dao
abstract class OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<OrderEntity>)

    @Query("UPDATE orders SET status = :status WHERE order_number = :order_number")
    abstract fun updateStatus(order_number: String, status: String)

    @Query("DELETE FROM orders WHERE data LIKE :data AND id_destination = :id_destination")
    abstract fun removeOrders(data: String, id_destination: Int)

    @Query("SELECT * FROM orders WHERE data LIKE :data AND id_destination = :id_destination")
    abstract fun getOrders(data: String, id_destination: Int): Maybe<List<OrderEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<OrderEntity>, data: String, id_destination: Int){
        removeOrders(data, id_destination)
        insert(entities)
    }
}