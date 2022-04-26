package com.tutuland.dogdroid.data.info.local

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

typealias DogInfoDatabase = DogInfoDao

fun makeDogInfoDatabase(context: Context): DogRoomDatabase = Room
    .databaseBuilder(context, DogRoomDatabase::class.java, "DogInfoDatabase")
    .fallbackToDestructiveMigration()
    .build()

@Database(entities = [DogInfoEntity::class], version = 1)
abstract class DogRoomDatabase : RoomDatabase() {
    abstract fun dogDao(): DogInfoDao
}

@Dao
interface DogInfoDao {
    @Query("SELECT * FROM DogInfoEntity ORDER BY breed ASC")
    fun getDogs(): Flow<List<DogInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDogs(vararg dogs: DogInfoEntity)

    @Query("DELETE FROM DogInfoEntity")
    suspend fun deleteDogs()
}

@Entity
data class DogInfoEntity(
    @PrimaryKey val breed: String,
    val imageUrl: String,
    val modifiedAt: Long,
)
