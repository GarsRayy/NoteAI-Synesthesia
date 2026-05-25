package com.example.synesthesia

import android.app.Application
import com.example.synesthesia.core.di.androidModule
import com.example.synesthesia.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

/**
 * Android Application class
 * 
 * Entry point untuk inisialisasi app-wide dependencies.
 */
class NoteAIApplication : Application() {
    
    companion object {
        lateinit var instance: NoteAIApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize Koin DI
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidLogger()
            androidContext(this@NoteAIApplication)
        }
    }
}
