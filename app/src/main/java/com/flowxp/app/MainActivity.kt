package com.flowxp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.flowxp.app.ui.FlowDestinations
import com.flowxp.app.ui.FlowXpNav
import com.flowxp.app.ui.theme.FlowXpTheme
import com.flowxp.app.util.UsageStatsHelper

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val start =
            if (UsageStatsHelper.hasUsageStatsPermission(this)) {
                FlowDestinations.Dashboard
            } else {
                FlowDestinations.Permission
            }
        setContent {
            FlowXpTheme {
                FlowXpNav(startDestination = start)
            }
        }
    }
}
