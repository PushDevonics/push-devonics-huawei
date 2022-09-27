package pro.devonics.push

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import pro.devonics.push.network.ApiHelper
import pro.devonics.push.network.RetrofitBuilder
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL
import java.util.*

private const val TAG = "MyHmsMessageService"

class MyHmsMessageService : HmsMessageService() {

    @RequiresApi(33)
    @SuppressLint("DiscouragedApi", "UnspecifiedImmutableFlag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //super.onMessageReceived(remoteMessage)
        remoteMessage.data.isNotEmpty().let {
            if (it) {
                Log.d(TAG, "Message data payload: ${remoteMessage.data}")
                /*try {
                    val pushModel: PushModel = Gson
                }*/
            }
        }
        val helperCache = HelperCache(this)
        val msgData = remoteMessage.dataOfMap
        val msgBody = msgData["message_body"].toString()
        Log.d(TAG, "onMessageReceived msgBody: $msgBody")
        if (msgData != null) {
            val sentPushId = msgData["sent_push_id"].toString()
            val deeplink = msgData["deeplink"].toString()
            if (remoteMessage.notification.link != null) {
                val openUrl = remoteMessage.notification.link.toString()
                helperCache.saveOpenUrl(openUrl)
                Log.d(TAG, "onMessageReceived openUrl: $openUrl")
            }

            //val openUrl = msgData["open_url"].toString()

            helperCache.saveSentPushId(sentPushId)
            helperCache.saveDeeplink(deeplink)

            Log.d(TAG, "onMessageReceived sentPushId: $sentPushId")
            Log.d(TAG, "onMessageReceived deeplink: $deeplink")

        }

        val packageName = applicationContext.packageName
        val mLauncher = "ic_launcher"
        val resId = resources.getIdentifier(mLauncher, "mipmap", packageName)

        val intent = packageManager.getLaunchIntentForPackage(packageName)
        //val intent = packageManager.getLaunchIntentForPackage(packageName)
        //intent?.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Send pushData to intent
        intent?.putExtra("sent_push_id", msgData["sent_push_id"]).toString()
        intent?.putExtra("deeplink", msgData["deeplink"]).toString()
        //intent?.putExtra("open_url", msgData["open_url"]).toString()
        val u = remoteMessage.notification.link
        intent?.putExtra("open_url", u).toString()

        //val largeIcon = remoteMessage.notification?.imageUrl.let { getBitmapFromUrl(it.toString()) }
        //val smallIcon = remoteMessage.notification?.icon.let { getBitmapFromUrl(it.toString()) }

        sendBroadcast(Intent(
            this,
            NotificationBroadcastReceiver::class.java)
            .putExtra("sent_push_id", msgData["sent_push_id"])
            .putExtra("deeplink", msgData["deeplink"])
            .putExtra("open_url", u?.toString())//msgData["open_url"])
            //.putExtra("largeIcon", remoteMessage.notification.imageUrl.toString())
            //.putExtra("smallIcon", remoteMessage.notification.icon)
            //.putExtra("title", remoteMessage.notification.title)
            //.putExtra("body", remoteMessage.notification.body)
        )
        //sendBroadcast(Intent(NotificationBroadcastReceiver.ACTION_PUSH))

        val rnds = (1..1000).random()

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, rnds, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            /*PendingIntent.getBroadcast(
                this, rnds, intent!!, PendingIntent.FLAG_ONE_SHOT)*/
            PendingIntent.getActivity(
                this, rnds, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val channelId = "Default"

        remoteMessage?.let {
            val map = it.dataOfMap
            for (key in map.keys) {
                //Log.d(TAG, "onMessageReceived: map k $key")
                Log.d(TAG, "onMessageReceived: map ${map[key]}")
                //Log.d(TAG, "onMessageReceived: map ${msg.dataOfMap["title"]}")
            }
            //Log.d(TAG, "onMessageReceived: map ${map["message"]}")
        }
        if (remoteMessage != null) {
            Log.v(TAG, "onMessageReceived: ${remoteMessage.dataOfMap.keys}")
        }
        //if (msg?.dataOfMap?.containsKey(""))
        if (remoteMessage!!.data.isNotEmpty()) {
            Log.i(TAG, "Message data payload: " + remoteMessage.data.toString())
        }
        if (remoteMessage.notification != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.notification.body)
        }

        //super.onMessageReceived(msg)
        Log.d(TAG, "getCollapseKey: " + remoteMessage?.collapseKey
                + "\n getData: " + remoteMessage?.data
                + "\n getFrom: " + remoteMessage?.from
                + "\n getTo: " + remoteMessage?.to
                + "\n getMessageId: " + remoteMessage?.messageId
                + "\n getSendTime: " + remoteMessage?.sentTime
                + "\n getMessageType: " + remoteMessage?.messageType
                + "\n getTtl: " + remoteMessage?.ttl
        )

        val notification = remoteMessage.notification
        Log.d(TAG, "onMessageReceived: notification $notification")
        val dataMsg = remoteMessage?.data
        Log.d(TAG, "onMessageReceived: dataMsg $dataMsg")
        if (notification != null) {
            Log.d(TAG, "\n getImageUrl: " + notification.imageUrl
                    + "\n getTitle: " + notification.title
                    + "\n getTitleLocalizationKey: " + notification.titleLocalizationKey
                    + "\n getTitleLocalizationArgs: " + Arrays.toString(notification.titleLocalizationArgs)
                    + "\n getBody: " + notification.body
                    + "\n getBodyLocalizationKey: " + notification.bodyLocalizationKey
                    + "\n getBodyLocalizationArgs: " + Arrays.toString(notification.bodyLocalizationArgs)
                    + "\n getIcon: " + notification.icon
                    + "\n getSound: " + notification.sound
                    + "\n getTag: " + notification.tag
                    + "\n getColor: " + notification.color
                    + "\n getClickAction: " + notification.clickAction
                    + "\n getChannelId: " + notification.channelId
                    + "\n getLink: " + notification.link
                    + "\n getNotifyId: " + notification.notifyId
            )
        }

        val notificationData = remoteMessage.dataOfMap
        if (notificationData != null) {
            Log.d(TAG, "onMessageReceived: notificationData $notificationData")
            if (notificationData.isEmpty()) {
                Log.d(TAG, "onMessageReceived: notification data is empty")
                return
            }

            val title = notification.title
            val text = notification.body
            Log.d(TAG, "onMessageReceived: title $title")

            //val iconUrl = remoteMessage.notification?.icon.toString()
            val imageUrl = remoteMessage.notification?.imageUrl.toString()

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(resId)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(this)
            if (remoteMessage.notification?.imageUrl != null) {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(imageUrl)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            notificationBuilder.setLargeIcon(resource)
                            notificationBuilder.setStyle(
                                NotificationCompat.BigPictureStyle().bigPicture(resource)
                            )
                            val notificationMy = notificationBuilder.build()
                            notificationManager.notify(1, notificationMy)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}

                    })
            } else {
                val notificationMy = notificationBuilder.build()
                notificationManager.notify(1, notificationMy)
            }
        }
    }

    /*@RequiresApi(33)
    @SuppressLint("DiscouragedApi", "UnspecifiedImmutableFlag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val helperCache = HelperCache(this)
        val msgData = remoteMessage.dataOfMap
        val msgBody = msgData["message_body"].toString()
        Log.d(TAG, "onMessageReceived msgBody: $msgBody")
        if (msgData != null) {
            val sentPushId = msgData["sent_push_id"].toString()
            val deeplink = msgData["deeplink"].toString()
            if (remoteMessage.notification.link != null) {
                val openUrl = remoteMessage.notification.link.toString()
                helperCache.saveOpenUrl(openUrl)
                Log.d(TAG, "onMessageReceived openUrl: $openUrl")
            }

            helperCache.saveSentPushId(sentPushId)
            helperCache.saveDeeplink(deeplink)

            Log.d(TAG, "onMessageReceived sentPushId: $sentPushId")
            Log.d(TAG, "onMessageReceived deeplink: $deeplink")

        }

        val packageName = applicationContext.packageName
        val mLauncher = "ic_launcher"
        val resId = resources.getIdentifier(mLauncher, "mipmap", packageName)

        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Send pushData to intent
        intent?.putExtra("sent_push_id", msgData["sent_push_id"]).toString()
        intent?.putExtra("deeplink", msgData["deeplink"]).toString()
        //intent?.putExtra("open_url", msgData["open_url"]).toString()
        val u = remoteMessage.notification.link
        intent?.putExtra("open_url", u).toString()

        //val largeIcon = remoteMessage.notification?.imageUrl.let { getBitmapFromUrl(it.toString()) }
        //val smallIcon = remoteMessage.notification?.icon.let { getBitmapFromUrl(it.toString()) }

        sendBroadcast(Intent(
            this,
            NotificationBroadcastReceiver::class.java)
            .putExtra("sent_push_id", msgData["sent_push_id"])
            .putExtra("deeplink", msgData["deeplink"])
            .putExtra("open_url", u?.toString())//msgData["open_url"])
        )
        //sendBroadcast(Intent(NotificationBroadcastReceiver.ACTION_PUSH))

        val rnds = (1..1000).random()

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, rnds, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(
                this, rnds, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val channelId = "Default"

        //super.onMessageReceived(msg)
        val notification = remoteMessage.notification

        val notificationData = remoteMessage.dataOfMap
        if (notificationData != null) {

            val title = notification.title
            val text = notification.body
            Log.d(TAG, "onMessageReceived: title $title")

            val imageUrl = remoteMessage.notification?.imageUrl.toString()

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(resId)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(this)
            if (remoteMessage.notification?.imageUrl != null) {
                Glide.with(applicationContext)
                    .asBitmap()
                    .load(imageUrl)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            notificationBuilder.setLargeIcon(resource)
                            notificationBuilder.setStyle(
                                NotificationCompat.BigPictureStyle().bigPicture(resource)
                            )
                            val notificationMy = notificationBuilder.build()
                            notificationManager.notify(1, notificationMy)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}

                    })
            } else {
                val notificationMy = notificationBuilder.build()
                notificationManager.notify(1, notificationMy)
            }
        }
    }*/

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        Log.d(TAG, "onNewToken: $p0")
        val service = ApiHelper(RetrofitBuilder.apiService)
        AppContextKeeper.setContext(applicationContext)
        val pushCache = PushCache()

        if (p0 != null) {
            service.updateRegistrationId(p0)
            pushCache.saveRegistrationIdPref(p0)
        }
        //sendTokenToDisplay(p0)
    }
}