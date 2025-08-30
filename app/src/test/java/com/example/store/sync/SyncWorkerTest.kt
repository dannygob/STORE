package com.example.store.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.store.data.local.AppDatabase
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.UserDao
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SyncWorkerTest {

    private lateinit var syncWorker: SyncWorker
    private val context: Context = mockk(relaxed = true)
    private val workerParameters: WorkerParameters = mockk(relaxed = true)
    private val appDatabase: AppDatabase = mockk()
    private val userDao: UserDao = mockk(relaxed = true)
    private val productDao: ProductDao = mockk(relaxed = true)
    private val firebaseAuth: FirebaseAuth = mockk()
    private val firestore: FirebaseFirestore = mockk()

    @Before
    fun setUp() {
        mockk_static(AppDatabase::class)
        every { AppDatabase.getDatabase(context) } returns appDatabase
        every { appDatabase.userDao() } returns userDao
        every { appDatabase.productDao() } returns productDao
        mockk_static(FirebaseAuth::class)
        every { FirebaseAuth.getInstance() } returns firebaseAuth
        mock_static(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns firestore

        syncWorker = SyncWorker(context, workerParameters)
    }

    @Test
    fun `doWork success when no unsynced data`() = runBlockingTest {
        // Given
        coEvery { userDao.getUnsyncedUsers() } returns emptyList()
        coEvery { productDao.getUnsyncedProducts() } returns emptyList()
        coEvery { firestore.collection("users").get().await() } returns mockk {
            every { documents } returns emptyList()
        }
        coEvery { firestore.collection("products").get().await() } returns mockk {
            every { toObjects<ProductEntity>() } returns emptyList()
        }

        // When
        val result = syncWorker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
    }

    @Test
    fun `doWork syncs unsynced users`() = runBlockingTest {
        // Given
        val unsyncedUser = UserEntity("1", "test@test.com", "password", "USER", true)
        coEvery { userDao.getUnsyncedUsers() } returns listOf(unsyncedUser)
        coEvery { productDao.getUnsyncedProducts() } returns emptyList()
        coEvery { firebaseAuth.fetchSignInMethodsForEmail(unsyncedUser.email).await().signInMethods } returns emptyList()
        coEvery { firebaseAuth.createUserWithEmailAndPassword(unsyncedUser.email, any()).await() } returns mockk {
            every { user.uid } returns "new_uid"
        }
        coEvery { firestore.collection("users").document("new_uid").set(any()).await() } returns mockk()
        coEvery { firestore.collection("users").get().await() } returns mockk {
            every { documents } returns emptyList()
        }
        coEvery { firestore.collection("products").get().await() } returns mockk {
            every { toObjects<ProductEntity>() } returns emptyList()
        }

        // When
        val result = syncWorker.doWork()

        // Then
        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { userDao.insertUser(any()) }
    }
}
