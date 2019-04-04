package com.example.mrkanapka_sprzedawca.database.dao

import android.arch.persistence.room.*
import com.example.mrkanapka_sprzedawca.database.entity.DestinationsEntity
import io.reactivex.Maybe


@Dao
abstract class DestinationsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<DestinationsEntity>)

    @Query("DELETE FROM destinations ")
    abstract fun removeDestinations()

    @Query("SELECT * FROM destinations")
    abstract fun getDestinations(): Maybe<List<DestinationsEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<DestinationsEntity>){
        removeDestinations()
        insert(entities)
    }
}