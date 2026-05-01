package com.flowxp.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: FlowUiState,
    onNavigateAppSelect: () -> Unit,
    onNavigateHistory: () -> Unit,
    onLostPermission: () -> Unit,
) {
    LaunchedEffect(state.hydrated, state.hasUsagePermission) {
        if (state.hydrated && !state.hasUsagePermission) onLostPermission()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FlowXP") },
                actions = {
                    TextButton(onClick = onNavigateAppSelect) { Text("앱 선택") }
                    TextButton(onClick = onNavigateHistory) { Text("기록") }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text("지금은 레벨업 중입니다", style = MaterialTheme.typography.titleLarge)
            }
            item {
                Text(
                    text = "Level ${state.progress.level}",
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
            item {
                val cur = state.progress.currentXp.coerceIn(0, 100)
                Text(
                    text = "XP $cur / 100",
                    style = MaterialTheme.typography.bodyLarge,
                )
                LinearProgressIndicator(
                    progress = { cur / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }
            item {
                Text(
                    text = "오늘 SNS 사용: ${state.todayMinutes}분",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item {
                Text(
                    text = "오늘 획득 XP(예상): ${state.todayPreviewXp} (자정 이후 확정)",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item {
                Text(
                    text = "스트릭: ${state.progress.streak}일",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            item {
                Text("앱별 사용 시간 (오늘)", style = MaterialTheme.typography.titleLarge)
            }
            if (state.perAppMinutes.isEmpty()) {
                item {
                    Text(
                        "추적 중인 앱이 없습니다. 앱 선택에서 SNS를 체크하세요.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                items(state.perAppMinutes, key = { it.first }) { (name, min) ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("$name — ${min}분", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
