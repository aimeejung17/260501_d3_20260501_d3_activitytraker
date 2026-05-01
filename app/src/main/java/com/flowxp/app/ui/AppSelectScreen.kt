package com.flowxp.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flowxp.app.model.TrackedApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectScreen(
    viewModel: FlowXpViewModel,
    onBack: () -> Unit,
) {
    val items = remember { mutableStateListOf<TrackedApp>() }

    LaunchedEffect(Unit) {
        items.clear()
        items.addAll(viewModel.trackedAppsForEditor())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("추적 앱 선택") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("닫기") }
                },
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        viewModel.saveTrackedApps(items.toList())
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Text("저장")
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(items.size, key = { items[it].packageName }) { index ->
                val app = items[index]
                ListItem(
                    headlineContent = { Text(app.appName) },
                    supportingContent = { Text(app.packageName) },
                    trailingContent = {
                        Checkbox(
                            checked = app.isSelected,
                            onCheckedChange = { checked ->
                                items[index] = app.copy(isSelected = checked)
                            },
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
