package com.example.allthethings.ui.core

import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.allthethings.NotificationJobService
import com.example.allthethings.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_core.*





const val CHANNEL_ID = "Notification"
const val NOTIFICATION_ID = 1
const val JOB_ID = 0

class GalleryFragment : Fragment() {

    private lateinit var coreViewModel: CoreViewModel
    private lateinit var mScheduler : JobScheduler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        coreViewModel =
            ViewModelProviders.of(this).get(CoreViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_core, container, false)
        val textView: TextView = root.findViewById(R.id.text_core)
        coreViewModel.text.observe(this, Observer {
            textView.text = it
        })
        // Job Scheduler
        mScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        return root
    }

    override fun onStart() {
        super.onStart()


        // Toast
        buttonToast.setOnClickListener {
            Toast.makeText(context, "This is a toast", Toast.LENGTH_LONG).show()
            // toast.setGravity(Gravity.TOP or Gravity.LEFT, 0, 0)
        }

        // Snackbar
        buttonSnackbar.setOnClickListener {
            Snackbar.make(it, "This is a snackbar", Snackbar.LENGTH_LONG).show()
        }

        // Notification
        buttonNotification.setOnClickListener {
            // Create an explicit intent for an Activity in your app
            val intent = Intent(it.context, GalleryFragment::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(it.context, 0, intent, 0)

            // Builder of the Notification
            val builder = NotificationCompat.Builder(it.context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_menu_send)
                .setContentTitle("My notification")
                .setContentText("Click to delete")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setVibrate(longArrayOf(0, 500))
                .setAutoCancel(true)

            // Show the Notification
            with(NotificationManagerCompat.from(it.context)) {
                // notificationId is a unique int for each notification that you must define
                notify(NOTIFICATION_ID, builder.build())
            }
        }

        // JobScheduler
        button_schedule.setOnClickListener {
            scheduleJob()
        }

        button_cancel.setOnClickListener {
            cancelJob()
        }
    }

    fun scheduleJob(){
        val selectedNetworkID = radioGroup.checkedRadioButtonId
        var selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE

        when (selectedNetworkID) {
            R.id.radio_none -> selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE
            R.id.radio_any -> selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY
            R.id.radio_wifi -> selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED
        }

        val serviceName = ComponentName(
            context!!.packageName,
            NotificationJobService::class.java.name
        )

        // JobInfo Builder
        val builder = JobInfo.Builder(JOB_ID, serviceName)
            .setRequiredNetworkType(selectedNetworkOption)

        // Constraints for the Job
        val constraintSet : Boolean = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE

        if (constraintSet) {
            val myJobInfo = builder.build()
            mScheduler.schedule(myJobInfo)
            Toast.makeText(context, R.string.job_scheduled, Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(
                context, R.string.no_constraint_toast,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun cancelJob() {
        if (mScheduler != null) {
            mScheduler.cancelAll()
            Toast.makeText(context, R.string.jobs_canceled, Toast.LENGTH_SHORT)
                .show()
        }
    }


}


