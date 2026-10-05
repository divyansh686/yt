package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.TransferCategory
import com.example.data.model.TransferSessionState
import com.example.data.model.TransferStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _sessionState = MutableStateFlow<TransferSessionState>(TransferSessionState.Idle)
    val sessionState = _sessionState.asStateFlow()

    private val _isOldPhone = MutableStateFlow<Boolean?>(null)
    val isOldPhone = _isOldPhone.asStateFlow()

    private val _categories = MutableStateFlow<List<TransferCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    fun setRole(isOld: Boolean) {
        _isOldPhone.value = isOld
        _sessionState.value = TransferSessionState.Searching
        
        // Mock connection process
        viewModelScope.launch {
            delay(2000)
            _sessionState.value = TransferSessionState.Connected
            delay(1000)
            startScanning()
        }
    }

    private fun startScanning() {
        _sessionState.value = TransferSessionState.Scanning
        viewModelScope.launch {
            val mockCategories = listOf(
                TransferCategory("photos", "Photos & Videos", Icons.Default.PhotoLibrary, 8_700_000_000, 9842),
                TransferCategory("music", "Music", Icons.Default.MusicNote, 1_800_000_000, 1284),
                TransferCategory("docs", "Documents", Icons.Default.Description, 2_100_000_000, 3421),
                TransferCategory("contacts", "Contacts", Icons.Default.Contacts, 4_000_000, 1245),
                TransferCategory("sms", "SMS Messages", Icons.Default.Sms, 12_000_000, 8432),
                TransferCategory("calls", "Call History", Icons.Default.Call, 2_000_000, 542),
                TransferCategory("calendar", "Calendar", Icons.Default.Event, 1_000_000, 124),
                TransferCategory("apps", "Apps", Icons.Default.Apps, 4_200_000_000, 42)
            )
            
            // Mock incremental scanning
            val scanningList = mutableListOf<TransferCategory>()
            mockCategories.forEach {
                delay(300)
                scanningList.add(it.copy(status = TransferStatus.COMPLETED))
                _categories.value = scanningList.toList()
            }
            
            _sessionState.value = TransferSessionState.Ready
        }
    }

    fun toggleCategory(id: String) {
        _categories.value = _categories.value.map {
            if (it.id == id) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun startTransfer() {
        val selected = _categories.value.filter { it.isSelected }
        if (selected.isEmpty()) return

        _sessionState.value = TransferSessionState.Transferring(0f, selected.first().name)
        
        viewModelScope.launch {
            var currentProgress = 0f
            selected.forEach { category ->
                _sessionState.value = TransferSessionState.Transferring(currentProgress, category.name)
                // Mock transfer for each category
                for (i in 1..10) {
                    delay(200)
                    val categoryProgress = i / 10f
                    val overallProgress = (selected.indexOf(category) + categoryProgress) / selected.size
                    _sessionState.value = TransferSessionState.Transferring(overallProgress, category.name)
                }
            }
            _sessionState.value = TransferSessionState.Completed
        }
    }

    fun reset() {
        _isOldPhone.value = null
        _sessionState.value = TransferSessionState.Idle
        _categories.value = emptyList()
    }
}
