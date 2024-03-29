package com.github.pwoicik.uekschedule.repository.mapper

import com.github.pwoicik.uekschedule.common.util.convertDateTime
import com.github.pwoicik.uekschedule.common.util.ifBlankOrNull
import com.github.pwoicik.uekschedule.common.util.toUppercase
import com.github.pwoicik.uekschedule.data.api.dto.ClassDto
import com.github.pwoicik.uekschedule.data.db.entity.ClassEntity
import com.github.pwoicik.uekschedule.domain.model.Class
import com.github.pwoicik.uekschedule.domain.model.ScheduleEntry

internal fun ClassEntity.toClass() = Class(
    schedulableId,
    subject,
    startDateTime,
    endDateTime,
    type,
    details,
    teachers,
    groups,
    location
)

internal fun Class.toClassEntity() = ClassEntity(
    schedulableId,
    subject,
    startDateTime,
    endDateTime,
    type,
    details,
    teachers,
    groups,
    location
)

internal fun List<ClassDto>.toClassEntities(groupId: Long): List<ClassEntity> = this
    .filter { dto ->
        val isInvalid = dto.date.isEmpty() || dto.startTime.isEmpty() || dto.endTime.isEmpty()
        !isInvalid
    }
    .map { dto ->
        val endTime = dto.endTime.replace(""" \(.+\)""".toRegex(), "")
        val teachers = dto.teachers
            ?.filter { it.teacher.isNotEmpty() }
            ?.map { it.teacher }
            ?.ifEmpty { null }
        val groups = dto.groups
            ?.split(", ")
            ?.ifEmpty { null }

        ClassEntity(
            schedulableId = groupId,
            subject = dto.subject.ifBlankOrNull { dto.type.toUppercase() },
            startDateTime = convertDateTime(dto.date, dto.startTime),
            endDateTime = convertDateTime(dto.date, endTime),
            type = dto.type,
            details = dto.details,
            teachers = teachers,
            groups = groups,
            location = dto.location.ifEmpty { null }
        )
    }

internal fun ClassEntity.toScheduleEntry() = ScheduleEntry(
    name = subject,
    startDateTime = startDateTime.toLocalDateTime(),
    endDateTime = endDateTime.toLocalDateTime(),
    type = type,
    details = details,
    teachers = teachers ?: emptyList(),
    groups = groups ?: emptyList(),
    location = location
)
