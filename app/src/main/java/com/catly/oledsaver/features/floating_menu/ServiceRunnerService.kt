package com.catly.oledsaver.features.floating_menu

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService

class ServiceRunnerService: JobIntentService() {

    companion object {
        private const val JOB_ID = 321

        fun enqueueWork(cxt: Context, intent: Intent){
            enqueueWork(cxt,ServiceRunnerService::class.java,JOB_ID,intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        startService(intent)
    }
}