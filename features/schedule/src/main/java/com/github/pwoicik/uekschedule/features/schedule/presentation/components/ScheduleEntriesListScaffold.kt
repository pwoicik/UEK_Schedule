package com.github.pwoicik.uekschedule.features.schedule.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerticalAlignCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.pwoicik.uekschedule.common.R
import com.github.pwoicik.uekschedule.presentation.components.SmallTopBarWithSearch
import com.github.pwoicik.uekschedule.presentation.components.SmallTopBarWithSearchColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
internal fun ScheduleEntriesListScaffold(
    title: String,
    isSearchFieldVisible: Boolean,
    dismissSearchField: () -> Unit,
    searchValue: TextFieldValue,
    onSearchValueChange: (TextFieldValue) -> Unit,
    isFabVisible: Boolean,
    onFabClick: () -> Unit,
    isRefreshing: Boolean,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onSearchValueClear: () -> Unit = { onSearchValueChange(TextFieldValue()) },
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: ScheduleEntriesListScaffoldColors = ScheduleEntriesListScaffoldColors.default(),
    content: @Composable BoxScope.() -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        topBar = {
            SmallTopBarWithSearch(
                title = { Text(title) },
                isSearchFieldVisible = isSearchFieldVisible,
                dismissSearchField = dismissSearchField,
                searchValue = searchValue,
                onSearchValueChange = onSearchValueChange,
                onSearchValueClear = {
                    onSearchValueClear()
                    keyboardController?.hide()
                },
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors.topBarColors
            )
        },
        floatingActionButton = {
            if (isFabVisible) {
                FloatingActionButton(
                    containerColor = colors.fabContainerColor,
                    contentColor = colors.fabContentColor,
                    onClick = onFabClick
                ) {
                    Icon(
                        imageVector = Icons.Default.VerticalAlignCenter,
                        contentDescription = stringResource(R.string.scroll_to_today)
                    )
                }
            }
        },
        snackbarHost = {
            com.github.pwoicik.uekschedule.presentation.components.SnackbarHost(
                snackbarHostState
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
            AnimatedVisibility(
                visible = isRefreshing,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                com.github.pwoicik.uekschedule.presentation.components.CircularProgressIndicator(
                    isSpinning = isRefreshing,
                    containerColor = colors.fabContainerColor,
                    contentColor = colors.fabContentColor,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
    }
}

internal data class ScheduleEntriesListScaffoldColors(
    val topBarColors: SmallTopBarWithSearchColors,
    val fabContainerColor: Color,
    val fabContentColor: Color
) {
    companion object {
        @Composable
        fun default(
            topBarColors: SmallTopBarWithSearchColors = SmallTopBarWithSearchColors.default(),
            fabContainerColor: Color = topBarColors.containerColor,
            fabContentColor: Color = topBarColors.titleColor
        ): ScheduleEntriesListScaffoldColors = ScheduleEntriesListScaffoldColors(
            topBarColors, fabContainerColor, fabContentColor
        )
    }
}
