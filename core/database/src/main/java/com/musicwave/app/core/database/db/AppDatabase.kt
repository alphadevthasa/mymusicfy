package com.musicwave.app.core.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.musicwave.app.core.database.dao.FavoriteDao
import com.musicwave.app.core.database.entity.FavoriteTrackEntity

@Database(
    entities = [FavoriteTrackEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
