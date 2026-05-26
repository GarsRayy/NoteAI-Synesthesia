package com.example.synesthesia.core.di

import com.example.synesthesia.core.util.AndroidNetworkMonitor
import com.example.synesthesia.core.util.DatabaseDriverFactory
import com.example.synesthesia.core.util.NetworkMonitor
import com.example.synesthesia.data.local.datastore.DataStoreFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 *
 * Menyediakan dependencies yang membutuhkan `Context`:
 * - DatabaseDriverFactory: untuk SQLDelight driver
 * - DataStoreFactory     : untuk lokasi file preferences
 * - NetworkMonitor       : untuk status koneksi
 */
val androidModule = module {
    single { DatabaseDriverFactory(androidContext()) }
    single { DataStoreFactory(androidContext()) }
    single { AndroidNetworkMonitor(androidContext()) } bind NetworkMonitor::class
}
