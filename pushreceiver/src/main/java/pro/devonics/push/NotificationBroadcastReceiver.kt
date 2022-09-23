package pro.devonics.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG = "NotificationBroadcastRe"

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")
        val bundle = intent?.extras
        val sentPushId = bundle?.getString("sent_push_id")
        val deeplink = bundle?.getString("deeplink")
        val openUrl = bundle?.getString("open_url")
        Log.d(TAG, "onReceive: sentPushId $sentPushId")
        Log.d(TAG, "onReceive: deeplink $deeplink")
        Log.d(TAG, "onReceive: openUrl $openUrl")
        val helperCache = context?.let { HelperCache(it) }
        helperCache?.saveSentPushId(sentPushId)
        if (deeplink != null) {
            helperCache?.saveDeeplink(deeplink)
        }
        helperCache?.saveOpenUrl(openUrl)
//        intent?.let {
//            val sentPushId = it.getStringExtra("sent_push_id")
//            val deeplink = it.getStringExtra("deeplink")
//            val openUrl = it.getStringExtra("open_url")
//            Log.d(TAG, "onReceive: sentPushId $sentPushId")
//            Log.d(TAG, "onReceive: deeplink $deeplink")
//            Log.d(TAG, "onReceive: openUrl $openUrl")

//            intent?.putExtra("sent_push_id", msgData["sent_push_id"]).toString()
//            intent?.putExtra("deeplink", msgData["deeplink"]).toString()
//            intent?.putExtra("open_url", msgData["open_url"]).toString()
//        }
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        /*intent?.let {
            val title = it.getStringExtra(NOTIFICATION_TITLE)
            val message = it.getStringExtra(NOTIFICATION_MESSAGE)
            Log.d(TAG, "onReceive: title $title")
            Log.d(TAG, "onReceive: message $message")

            val notificationData = Data.Builder()
                .putString(NOTIFICATION_TITLE, title)
                .putString(NOTIFICATION_MESSAGE, message)
                .build()

            // Init Worker
            val work = OneTimeWorkRequest.Builder(ScheduledWorker::class.java)
                .setInputData(notificationData)
                .build()

            // Start Worker
            WorkManager.getInstance().beginWith(work).enqueue()
            Log.d(TAG, "WorkManager is Enqueued")
        }*/
    }

    companion object {
        const val ACTION_PUSH = "pro.devonics.action.PUSH"
    }
}