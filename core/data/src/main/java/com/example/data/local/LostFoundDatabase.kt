package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LostFoundDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
