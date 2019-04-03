package com.example.mrkanapka_sprzedawca.database.dao

import android.arch.persistence.room.*
import com.example.mrkanapka_sprzedawca.database.entity.DateEntity
import io.reactivex.Maybe

@Dao
abstract class DateDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: List<DateEntity>)

    @Query("DELETE FROM dates WHERE date LIKE :date")
    abstract fun removeDate(date: String)

    @Query("SELECT * FROM dates WHERE date LIKE :date")
    abstract fun getDate(date: String): Maybe<List<DateEntity>>

    @Transaction
    open fun removeAndInsert(entities: List<DateEntity>, date: String){
        removeDate(date)
        insert(entities)
    }
}