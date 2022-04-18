package com.github.pwoicik.uekschedule.feature_schedule.data.repository

import androidx.room.withTransaction
import com.github.pwoicik.uekschedule.feature_schedule.data.api.ScheduleApi
import com.github.pwoicik.uekschedule.feature_schedule.data.api.dto.GroupDto
import com.github.pwoicik.uekschedule.feature_schedule.data.api.mapper.toGroup
import com.github.pwoicik.uekschedule.feature_schedule.data.api.mapper.toGroupWithClasses
import com.github.pwoicik.uekschedule.feature_schedule.data.db.ScheduleDatabase
import com.github.pwoicik.uekschedule.feature_schedule.data.db.entity.Activity
import com.github.pwoicik.uekschedule.feature_schedule.data.db.entity.Class
import com.github.pwoicik.uekschedule.feature_schedule.data.db.entity.Group
import com.github.pwoicik.uekschedule.feature_schedule.data.db.entity.GroupWithClasses
import com.github.pwoicik.uekschedule.feature_schedule.data.db.mapper.toScheduleEntries
import com.github.pwoicik.uekschedule.feature_schedule.data.db.mapper.toScheduleEntry
import com.github.pwoicik.uekschedule.feature_schedule.data.db.mapper.toSubject
import com.github.pwoicik.uekschedule.feature_schedule.data.db.mapper.toSubjectEntity
import com.github.pwoicik.uekschedule.feature_schedule.domain.model.ScheduleEntry
import com.github.pwoicik.uekschedule.feature_schedule.domain.model.Subject
import com.github.pwoicik.uekschedule.feature_schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepositoryImpl @Inject constructor(
    private val scheduleApi: ScheduleApi,
    private val scheduleDatabase: ScheduleDatabase
) : ScheduleRepository {

    private val classDao = scheduleDatabase.classDao
    private val groupDao = scheduleDatabase.groupDao
    private val activityDao = scheduleDatabase.activityDao
    private val subjectDao = scheduleDatabase.subjectDao

    override suspend fun addSubjectToIgnored(subject: Subject) {
        subjectDao.insertSubject(subject.toSubjectEntity())
    }

    override suspend fun deleteActivity(activity: Activity) {
        activityDao.deleteActivity(activity)
    }

    override suspend fun deleteGroup(group: Group) {
        groupDao.deleteGroup(group)
    }

    override suspend fun deleteSubjectFromIgnored(subject: Subject) {
        subjectDao.deleteSubject(subject.toSubjectEntity())
    }

    private suspend fun fetchSchedule(group: Group): GroupWithClasses {
        return scheduleApi.getSchedule(group.id).toGroupWithClasses()
    }

    override suspend fun fetchSchedule(groupId: Long): Result<List<ScheduleEntry>> = try {
        Result.success(
            scheduleApi.getSchedule(groupId)
                .toGroupWithClasses()
                .classes.map(Class::toScheduleEntry)
        )
    } catch (e: Exception) {
        Timber.e(e)
        Result.failure(e)
    }

    override suspend fun getActivity(id: Long): Activity {
        return activityDao.getActivity(id)
    }

    override fun getAllActivities(): Flow<List<Activity>> {
        return activityDao.getAllActivities()
    }

    override suspend fun getAllGroups(): Result<List<Group>> = try {
        Result.success(
            scheduleApi.getGroups().groups!!.map(GroupDto::toGroup)
        )
    } catch (e: Exception) {
        Timber.e(e)
        Result.failure(e)
    }

    override fun getAllScheduleEntries(): Flow<List<ScheduleEntry>> {
        val classes = classDao.getAllClasses()
            .map { it.map(Class::toScheduleEntry) }
        val activities = activityDao.getAllActivities()
            .map {
                it.map(Activity::toScheduleEntries)
                    .flatten()
            }

        @Suppress("NAME_SHADOWING")
        return combine(classes, activities) { classes, activities ->
            (classes + activities).sortedBy(ScheduleEntry::startDateTime)
        }
    }

    override fun getAllSubjectsForGroup(groupId: Long): Flow<List<Subject>> = flow {
        val allSubjects = subjectDao.getAllSubjectsForGroup(groupId)

        subjectDao.getAllIgnoredSubjectsForGroup(groupId)
            .map { ignoredSubjects ->
                allSubjects.map { it.toSubject(it in ignoredSubjects) }
            }
            .collect(::emit)
    }

    override suspend fun getGroupWithClasses(group: Group): GroupWithClasses {
        return groupDao.getGroupWithClasses(group.id)
    }

    override fun getSavedGroups(): Flow<List<Group>> {
        return groupDao.getAllGroups()
    }

    override fun getSavedGroupsCount(): Flow<Int> {
        return groupDao.getGroupsCount()
    }

    override suspend fun saveActivity(activity: Activity) {
        activityDao.insertActivity(activity)
    }

    override suspend fun saveGroup(group: Group): Result<Unit> = try {
        val gwc = fetchSchedule(group)
        scheduleDatabase.withTransaction {
            groupDao.insertGroup(gwc.group)
            classDao.insertAllClasses(gwc.classes)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e)
        Result.failure(e)
    }

    override suspend fun saveGroupWithClasses(gwc: GroupWithClasses) {
        scheduleDatabase.withTransaction {
            groupDao.insertGroup(gwc.group)
            classDao.insertAllClasses(gwc.classes)
        }
    }

    override suspend fun updateGroup(group: Group) {
        groupDao.updateGroup(group)
    }

    override suspend fun updateSchedules(): Result<Unit> = try {
        val groups = getSavedGroups().first()
        val groupsWithClasses = groups.map { group ->
            Timber.d("fetching schedule for group ${group.name}")
            fetchSchedule(group)
        }
        scheduleDatabase.withTransaction {
            classDao.deleteAllClasses()
            groupsWithClasses.forEach { (_, classes) ->
                classDao.insertAllClasses(classes)
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e)
        Result.failure(e)
    }
}
