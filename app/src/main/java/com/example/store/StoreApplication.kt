package com.example.store

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.store.data.repository.AuthRepositoryImpl
import com.example.store.sync.SyncWorker
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class StoreApplication : Application() {

    @Inject
    lateinit var authRepository: AuthRepositoryImpl

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        schedulePeriodicSync()
        CoroutineScope(Dispatchers.IO).launch {
            authRepository.createInitialAdminUser()
        }
    }

    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            1, TimeUnit.HOURS // Run every hour
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(periodicSyncRequest)
    }
}
