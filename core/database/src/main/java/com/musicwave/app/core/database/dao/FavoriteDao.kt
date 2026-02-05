package com.musicwave.app.core.database.dao

import androidx.room.*
import com.musicwave.app.core.database.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY added_at DESC")
    fun getAll(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getById(id: Long): FavoriteTrackEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    fun isFavorite(id: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: FavoriteTrackEntity)

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM favorites")
    suspend fun clear()
}
