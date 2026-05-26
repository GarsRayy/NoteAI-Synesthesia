package com.example.synesthesia.core.util

import kotlinx.coroutines.flow.Flow

/**
 * Interface untuk memonitor status koneksi internet
 */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
