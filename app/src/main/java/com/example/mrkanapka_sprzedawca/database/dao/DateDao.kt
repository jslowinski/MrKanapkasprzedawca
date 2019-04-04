package com.example.mrkanapka_sprzedawca.database.dao

import android.arch.persistence.room.*
import com.example.mrkanapka_sprzedawca.database.entity.DateEntity
import io.reactivex.Maybe

@Dao
abstract class DateDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<DateEntity>)

    @Query("DELETE FROM dates WHERE id_destination = :id_destination")
    abstract fun removeDate(id_destination: Int)

    @Query("SELECT * FROM dates WHERE id_destination =  :id_destination")
    abstract fun getDate(id_destination: Int): Maybe<List<DateEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<DateEntity>, id_destination: Int){
        removeDate(id_destination)
        insert(entities)
    }
}