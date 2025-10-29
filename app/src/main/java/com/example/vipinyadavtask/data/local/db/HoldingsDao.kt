package com.example.vipinyadavtask.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HoldingsDao {
    @Query("SELECT * FROM holdings")
    suspend fun getAll(): List<HoldingEntity>

    @Query("DELETE FROM holdings")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<HoldingEntity>)
}


