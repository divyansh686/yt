package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.TransferSessionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    state: TransferSessionState,
    isOldPhone: Boolean,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isOldPhone) "Send Data" else "Receive Data") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Usb,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            val title = when (state) {
                TransferSessionState.Searching -> "Connect USB Cable"
                is TransferSessionState.Error -> "Connection Error"
                else -> "Connecting..."
            }

            val description = when (state) {
                TransferSessionState.Searching -> {
                    if (isOldPhone) {
                        "Connect this phone to the new phone using a USB cable."
                    } else {
                        "Connect this phone to the old phone using a USB cable."
                    }
                }
                is TransferSessionState.Error -> state.message
                else -> "Waiting for authorization..."
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state is TransferSessionState.Searching) {
                Spacer(modifier = Modifier.height(48.dp))
                CircularProgressIndicator()
            }
        }
    }
}
