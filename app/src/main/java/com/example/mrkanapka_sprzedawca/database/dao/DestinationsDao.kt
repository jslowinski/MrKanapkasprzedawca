package com.example.mrkanapka_sprzedawca.database.dao

import android.arch.persistence.room.*
import com.example.mrkanapka_sprzedawca.database.entity.DestinationsEntity
import io.reactivex.Maybe


@Dao
abstract class DestinationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<DestinationsEntity>)

    @Query("DELETE FROM destinations WHERE id_destination = :id_destination")
    abstract fun removeDestinations(id_destination: Int)

    @Query("SELECT * FROM destinations WHERE id_destination = :id_destination")
    abstract fun getDestinations(id_destination: Int): Maybe<List<DestinationsEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<DestinationsEntity>, id_destination: Int){
        removeDestinations(id_destination)
        insert(entities)
    }
}