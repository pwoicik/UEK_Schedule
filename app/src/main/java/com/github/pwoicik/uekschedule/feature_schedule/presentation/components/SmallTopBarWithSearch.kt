package com.github.pwoicik.uekschedule.feature_schedule.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.github.pwoicik.uekschedule.R
import com.google.accompanist.insets.statusBarsPadding

@Composable
fun SmallTopBarWithSearch(
    title: @Composable () -> Unit,
    isSearchFieldVisible: Boolean,
    searchValue: TextFieldValue,
    onSearchValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onSearchValueClear: () -> Unit = { onSearchValueChange(TextFieldValue()) },
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
    navigationIconContentColor: Color = MaterialTheme.colorScheme.onSurface,
    actionIconContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Surface(
        color = containerColor,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .animateContentSize()
        ) {
            SmallTopAppBar(
                title = title,
                actions = actions,
                navigationIcon = navigationIcon,
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = titleContentColor,
                    navigationIconContentColor = navigationIconContentColor,
                    actionIconContentColor = actionIconContentColor
                )
            )
            AnimatedVisibility(
                visible = isSearchFieldVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                SearchTextField(
                    value = searchValue,
                    onValueChange = onSearchValueChange,
                    onClearText = onSearchValueClear,
                    placeholder = stringResource(R.string.entry_search_placeholder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .focusRequester(focusRequester)
                )
            }
        }
    }
}
