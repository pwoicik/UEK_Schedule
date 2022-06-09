package com.github.pwoicik.uekschedule.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "ignored_subjects",
    primaryKeys = [
        "schedulable_id",
        "name",
        "type"
    ]
)
data class SubjectEntity(

    @ColumnInfo(name = "schedulable_id")
    val groupId: Long,

    @ColumnInfo(name = "group_name")
    val groupName: String,

    val name: String,

    val type: String
)
