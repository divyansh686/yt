package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.data.model.TransferSessionState
import com.example.ui.MainViewModel
import com.example.ui.screens.ConnectionScreen
import com.example.ui.screens.DataSelectionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReportScreen
import com.example.ui.screens.TransferProgressScreen
import com.example.ui.theme.PhoneMoveTheme
import com.example.util.PermissionRequester

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhoneMoveTheme {
                val sessionState by viewModel.sessionState.collectAsState()
                val isOldPhone by viewModel.isOldPhone.collectAsState()
                val categories by viewModel.categories.collectAsState()

                when (val state = sessionState) {
                    is TransferSessionState.Idle -> {
                        HomeScreen(
                            onSelectOldPhone = { viewModel.setRole(true) },
                            onSelectNewPhone = { viewModel.setRole(false) }
                        )
                    }
                    is TransferSessionState.Searching,
                    is TransferSessionState.Connected,
                    is TransferSessionState.Scanning,
                    is TransferSessionState.Error -> {
                        if (isOldPhone == true && state !is TransferSessionState.Error) {
                            PermissionRequester(onPermissionsGranted = {
                                // Permissions granted, logic continues in ViewModel mock for now
                            })
                        }
                        ConnectionScreen(
                            state = state,
                            isOldPhone = isOldPhone ?: true,
                            onBack = { viewModel.reset() }
                        )
                    }
                    is TransferSessionState.Ready -> {
                        DataSelectionScreen(
                            categories = categories,
                            onToggleCategory = { viewModel.toggleCategory(it) },
                            onContinue = { viewModel.startTransfer() },
                            onBack = { viewModel.reset() }
                        )
                    }
                    is TransferSessionState.Transferring -> {
                        TransferProgressScreen(
                            progress = state.progress,
                            currentCategory = state.currentCategory,
                            onCancel = { viewModel.reset() }
                        )
                    }
                    is TransferSessionState.Completed -> {
                        ReportScreen(
                            onFinish = { viewModel.reset() }
                        )
                    }
                }
            }
        }
    }
}
