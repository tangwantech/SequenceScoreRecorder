package com.example.sequencescorerecorder.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sequencescorerecorder.dataModels.AcademicYearsTypeConverter
import com.example.sequencescorerecorder.dataModels.StudentData

@Database(entities = [StudentData::class], version = 1)
@TypeConverters(AcademicYearsTypeConverter::class)
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
                    "sequence_score_database1.1"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}