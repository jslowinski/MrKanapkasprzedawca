package com.example.mrkanapka_sprzedawca.database.dao

import android.arch.persistence.room.*
import com.example.mrkanapka_sprzedawca.database.entity.TokenEntity
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class TokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entities: TokenEntity)

    @Query("DELETE FROM token")
    abstract fun removeToken()

    @Query("SELECT * FROM token")
    abstract fun getToken() : Single<TokenEntity>

    @Transaction
    open fun removeAndInsert(entities: TokenEntity) {
        removeToken()
        insert(entities)
    }
}