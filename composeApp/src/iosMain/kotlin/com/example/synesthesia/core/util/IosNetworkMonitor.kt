package com.example.synesthesia.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class IosNetworkMonitor : NetworkMonitor {
    override val isOnline: Flow<Boolean> = flowOf(true) // Placeholder
}
