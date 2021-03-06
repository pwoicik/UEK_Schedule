package com.github.pwoicik.uekschedule.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.pwoicik.uekschedule.data.db.converters.ScheduleDbTypeConverters
import com.github.pwoicik.uekschedule.data.db.dao.ActivityDao
import com.github.pwoicik.uekschedule.data.db.dao.ClassDao
import com.github.pwoicik.uekschedule.data.db.dao.GroupDao
import com.github.pwoicik.uekschedule.data.db.dao.SubjectDao
import com.github.pwoicik.uekschedule.data.db.entity.ActivityEntity
import com.github.pwoicik.uekschedule.data.db.entity.ClassEntity
import com.github.pwoicik.uekschedule.data.db.entity.GroupEntity
import com.github.pwoicik.uekschedule.data.db.entity.SubjectEntity

@Database(
    entities = [
        GroupEntity::class,
        ClassEntity::class,
        ActivityEntity::class,
        SubjectEntity::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        )
    ]
)
@TypeConverters(ScheduleDbTypeConverters::class)
abstract class ScheduleDatabase : RoomDatabase() {

    abstract val classDao: ClassDao
    abstract val groupDao: GroupDao
    abstract val activityDao: ActivityDao
    abstract val subjectDao: SubjectDao
}
