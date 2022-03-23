package com.github.pwoicik.uekschedule.feature_schedule.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.pwoicik.uekschedule.feature_schedule.data.db.dao.ActivityDao
import com.github.pwoicik.uekschedule.feature_schedule.data.db.dao.ClassDao
import com.github.pwoicik.uekschedule.feature_schedule.data.db.dao.GroupDao
import com.github.pwoicik.uekschedule.feature_schedule.data.util.ScheduleDbTypeConverters
import com.github.pwoicik.uekschedule.feature_schedule.data.db.entity.Activity
import com.github.pwoicik.uekschedule.feature_schedule.data.db.entity.Class
import com.github.pwoicik.uekschedule.feature_schedule.data.db.entity.Group
import com.github.pwoicik.uekschedule.feature_schedule.data.db.entity.IgnoredClass

@Database(
    entities = [
        Group::class,
        Class::class,
        Activity::class,
        IgnoredClass::class
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
}
