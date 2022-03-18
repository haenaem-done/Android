package com.palette.done

import android.app.Application
import com.palette.done.data.PreferenceManager
import com.palette.done.data.db.DoneDatabase
import com.palette.done.data.db.datasource.DoneRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DoneApplication : Application() {
    companion object {
        lateinit var pref: PreferenceManager
    }

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { DoneDatabase.getDatabase(this)}
    val doneRepository by lazy { DoneRepository(database!!.doneDao()) }


    override fun onCreate() {
        super.onCreate()
        pref = PreferenceManager(applicationContext)
    }
}