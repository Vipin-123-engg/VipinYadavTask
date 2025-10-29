package com.example.vipinyadavtask.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HoldingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun holdingsDao(): HoldingsDao
}


