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
    }

    companion object {
        const val ACTION_PUSH = "pro.devonics.action.PUSH"
    }
}