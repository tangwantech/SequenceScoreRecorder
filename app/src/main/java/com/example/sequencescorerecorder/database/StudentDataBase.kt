package com.example.sequencescorerecorder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sequencescorerecorder.dataModels.StudentData

@Database(entities = [StudentData::class], version = 1)
abstract class StudentDatabase: RoomDatabase() {

    abstract fun studentDataDao(): StudentDataDao

    companion object {
        @Volatile
        private var INSTANCE: StudentDatabase? = null

        fun getStudentDatabase(context: Context): StudentDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentDatabase::class.java,
                    "sequence_score_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}