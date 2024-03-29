package com.github.pwoicik.uekschedule.features.schedule.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.pwoicik.uekschedule.domain.model.ScheduleEntry
import com.github.pwoicik.uekschedule.presentation.util.openInBrowser
import com.github.pwoicik.uekschedule.resources.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun ScheduleEntriesList(
    scheduleEntries: Map<LocalDate, List<ScheduleEntry>>,
    timeNow: LocalDateTime,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = lazyListState,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        for ((date, entries) in scheduleEntries) {
            scheduleEntriesListStickyHeader(date)
            items(items = entries) { entry ->
                ScheduleEntriesListItem(scheduleEntry = entry, status = entry.status(timeNow))
                HorizontalDivider(modifier = Modifier.alpha(0.5f), thickness = Dp.Hairline)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.scheduleEntriesListStickyHeader(
    startDate: LocalDate
) {
    stickyHeader {
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = startDate.format(dateFormatter),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
            )
        }
    }
}

@Composable
internal fun ScheduleEntriesListItem(
    scheduleEntry: ScheduleEntry,
    status: ScheduleEntryStatus
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .alpha(
                if (status is ScheduleEntryStatus.Ended) 0.4f
                else 1f
            )
            .padding(10.dp)
    ) {
        ScheduleEntryTimesColumn(
            status = status,
            startDateTime = scheduleEntry.startDateTime,
            endDateTime = scheduleEntry.endDateTime
        )
        ScheduleEntrySummaryColumn(
            name = scheduleEntry.name,
            teachers = scheduleEntry.teachers,
            groups = scheduleEntry.groups,
            details = scheduleEntry.details,
            type = scheduleEntry.type,
            location = scheduleEntry.location
        )
        ScheduleEntryStatusColumn(status)
    }
}

@Composable
private fun ScheduleEntryTimesColumn(
    status: ScheduleEntryStatus,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime
) {
    Column {
        val startAlpha = if (status is ScheduleEntryStatus.InProgress) 0.6f else 1f
        val endAlpha = if (status is ScheduleEntryStatus.NotStarted) 0.6f else 1f
        Text(
            text = startDateTime.format(timeFormatter),
            modifier = Modifier.alpha(startAlpha)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = endDateTime.format(timeFormatter),
            modifier = Modifier.alpha(endAlpha)
        )
    }
}

@Composable
private fun RowScope.ScheduleEntrySummaryColumn(
    name: String,
    teachers: List<String>,
    groups: List<String>,
    details: String?,
    type: String?,
    location: String?
) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .weight(0.6f, fill = true)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = name,
            lineHeight = 21.sp,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyMedium,
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            if (teachers.isNotEmpty()) {
                Column {
                    teachers.forEach { teacher ->
                        Text(
                            text = teacher,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (groups.isNotEmpty()) {
                Column {
                    groups.forEach { group ->
                        Text(
                            text = group,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            CompositionLocalProvider(
                LocalContentColor provides
                        if (type == "Przeniesienie zajęć")
                            MaterialTheme.colorScheme.tertiary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                if (details == null) {
                    if (type != null) {
                        Text(
                            text = type,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.tertiary
                    ) {
                        Column {
                            if (type != null) {
                                Text(
                                    text = type,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(details)
                        }
                    }
                }

                if (location != null) {
                    val match = htmlAnchorRegex.find(location)
                    if (match != null) {
                        val url = match.groupValues[1]
                        val text = match.groupValues[2].trim()

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clickable { context.openInBrowser(url) }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = stringResource(R.string.open_in_browser),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = text,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.align(Alignment.Bottom)
                            )
                        }
                    } else {
                        Text(
                            text = location,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleEntryStatusColumn(
    status: ScheduleEntryStatus
) {
    Column(horizontalAlignment = Alignment.End) {
        ProvideTextStyle(MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.End)) {
            val text = getStatusText(status)
            Text(
                text = text,
                color = if (status is ScheduleEntryStatus.InProgress) {
                    MaterialTheme.colorScheme.primary
                } else {
                    LocalTextStyle.current.color
                }
            )
        }
    }
}

@Composable
private fun getStatusText(status: ScheduleEntryStatus) = when (status) {
    is ScheduleEntryStatus.NotStarted -> {
        when (val startsIn = status.minutesToStart) {
            in 1 until 60 -> {
                stringResource(R.string.class_starts_in_minutes, startsIn)
            }

            in 60 until 1440 -> {
                stringResource(R.string.class_starts_in_hours, startsIn / 60)
            }

            in 1440..Long.MAX_VALUE -> {
                val days = (startsIn / 1440).toInt()
                pluralStringResource(
                    id = R.plurals.class_starts_in_days,
                    count = days,
                    days
                )
            }

            else -> {
                stringResource(R.string.class_starts_in_less_than_1_min)
            }
        }
    }

    is ScheduleEntryStatus.InProgress -> {
        when (val endsIn = status.minutesRemaining) {
            in 1..Int.MAX_VALUE -> {
                stringResource(R.string.class_ends_in_minutes, endsIn)
            }

            else -> {
                stringResource(R.string.class_ends_in_less_than_1_min)
            }
        }
    }

    is ScheduleEntryStatus.Ended -> {
        stringResource(R.string.class_ended)
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("eee dd MMM yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

private val htmlAnchorRegex = """<a href="(.+)">(.+)</a>""".toRegex()
