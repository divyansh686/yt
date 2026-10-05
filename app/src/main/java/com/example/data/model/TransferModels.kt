package com.example.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class TransferCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val sizeBytes: Long = 0,
    val itemCount: Int = 0,
    val isSelected: Boolean = true,
    val status: TransferStatus = TransferStatus.IDLE
)

enum class TransferStatus {
    IDLE,
    SCANNING,
    PREPARING,
    TRANSFERRING,
    VERIFYING,
    COMPLETED,
    FAILED
}

data class DeviceInfo(
    val name: String,
    val model: String,
    val osVersion: String
)

sealed class TransferSessionState {
    object Idle : TransferSessionState()
    object Searching : TransferSessionState()
    object Connected : TransferSessionState()
    object Scanning : TransferSessionState()
    object Ready : TransferSessionState()
    data class Transferring(val progress: Float, val currentCategory: String) : TransferSessionState()
    object Completed : TransferSessionState()
    data class Error(val message: String) : TransferSessionState()
}
