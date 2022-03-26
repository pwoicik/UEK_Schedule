package com.github.pwoicik.uekschedule.feature_schedule.presentation.screens.groupSubjects

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.pwoicik.uekschedule.R
import com.github.pwoicik.uekschedule.feature_schedule.domain.model.Subject
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Destination(
    navGraph = "yourGroups",
    navArgsDelegate = GroupSubjectsNavArgs::class
)
@Composable
fun GroupSubjectsScreen(
    viewModel: GroupSubjectsViewModel = hiltViewModel()
) {
    val subjects by viewModel.subjects.collectAsState()

    Scaffold {
        Crossfade(subjects == null) { isFetching ->
            when (isFetching) {
                true -> {}
                false -> {
                    SubjectList(
                        subjects = subjects!!,
                        onSubjectIgnoreClick = { viewModel.toggleSubjectIgnore(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectList(
    subjects: List<Subject>,
    onSubjectIgnoreClick: (Subject) -> Unit
) {
    if (subjects.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Nothing here")
        }

    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(24.dp)
        ) {
            items(subjects) { subject ->
                Surface(
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                            .padding(start = 8.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(subject.name)
                            Text(
                                text = subject.type,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        IconButton(onClick = { onSubjectIgnoreClick(subject) }) {
                            Icon(
                                imageVector = if (subject.isIgnored) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = stringResource(R.string.hide_subject)
                            )
                        }
                    }
                }
            }
        }
    }
}