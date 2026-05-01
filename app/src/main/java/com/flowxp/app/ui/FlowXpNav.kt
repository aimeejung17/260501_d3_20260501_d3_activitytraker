package com.flowxp.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object FlowDestinations {
    const val Permission = "permission"
    const val Dashboard = "dashboard"
    const val AppSelect = "app_select"
    const val History = "history"
}

@Composable
fun FlowXpNav(startDestination: String) {
    val viewModel: FlowXpViewModel = viewModel()
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(FlowDestinations.Permission) {
            PermissionScreen(
                hasPermission = state.hasUsagePermission,
                onPermissionGranted = {
                    navController.navigate(FlowDestinations.Dashboard) {
                        popUpTo(FlowDestinations.Permission) { inclusive = true }
                    }
                },
            )
        }
        composable(FlowDestinations.Dashboard) {
            DashboardScreen(
                state = state,
                onNavigateAppSelect = { navController.navigate(FlowDestinations.AppSelect) },
                onNavigateHistory = { navController.navigate(FlowDestinations.History) },
                onLostPermission = {
                    navController.navigate(FlowDestinations.Permission) {
                        popUpTo(FlowDestinations.Dashboard) { inclusive = true }
                    }
                },
            )
        }
        composable(FlowDestinations.AppSelect) {
            AppSelectScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
        composable(FlowDestinations.History) {
            HistoryScreen(
                history = state.history,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
