package com.tutuland.dogdroid.data.source.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

typealias DogDatabase = DogDao

fun makeDogDatabase(context: Context): DogRoomDatabase = Room
    .databaseBuilder(context, DogRoomDatabase::class.java, "DogDatabase")
    .fallbackToDestructiveMigration()
    .build()

@Database(entities = [DogEntity::class], version = 1)
abstract class DogRoomDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
}

@Dao
interface DogDao {
    @Query("SELECT * FROM DogEntity ORDER BY breed ASC")
    fun getDogs(): Flow<List<DogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDogs(vararg dogs: DogEntity)

    @Query("DELETE FROM DogEntity")
    suspend fun deleteDogs()
}

@Entity
data class DogEntity(
    @PrimaryKey val breed: String,
    val imageUrl: String,
    val isFavorite: Boolean,
)
