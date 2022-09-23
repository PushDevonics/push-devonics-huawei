package pro.devonics.push

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
    private var b: Bitmap? = null

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
        //val helperCache = HelperCache(this)
        val msgData = remoteMessage.dataOfMap
        val msgBody = msgData["message_body"].toString()
        Log.d(TAG, "onMessageReceived msgBody: $msgBody")
        /*if (msgData != null) {
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

        }*/

        val packageName = applicationContext.packageName
        val mLauncher = "ic_launcher"
        val resId = resources.getIdentifier(mLauncher, "mipmap", packageName)

        val intent = packageManager.getLaunchIntentForPackage(packageName)
        //val intent = packageManager.getLaunchIntentForPackage(packageName)
        //intent?.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Send pushData to intent
        //intent?.putExtra("sent_push_id", msgData["sent_push_id"]).toString()
        //intent?.putExtra("deeplink", msgData["deeplink"]).toString()
        //intent?.putExtra("open_url", msgData["open_url"]).toString()
        val u = remoteMessage.notification.link

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
            /*PendingIntent.getBroadcast(
                this, rnds, intent!!, PendingIntent.FLAG_ONE_SHOT)*/
            PendingIntent.getActivity(
                this, rnds, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        val channelId = "Default"
        /*if ( remoteMessage?.notification?.imageUrl != null
            && remoteMessage.notification?.icon == null && remoteMessage.notification != null) {
            remoteMessage.notification.let {
                Log.d(TAG, "1111111")
                val builder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(resId)
                    .setContentTitle(remoteMessage.notification?.title)
                    .setContentText(remoteMessage.notification?.body)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(
                            //smallIcon
                            largeIcon//remoteMessage.notification?.imageUrl.let { getBitmapFromUrl(it) }
                        )
                    )
                    .setContentIntent(pendingIntent)

                val notificationManager = NotificationManagerCompat.from(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Default channel",
                        NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(1, builder.build())
            }
        }

        if (remoteMessage?.notification?.imageUrl != null
            && remoteMessage.notification?.icon != null && remoteMessage.notification != null) {
            remoteMessage.notification.let {
                Log.d(TAG, "222222")
                val builder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(resId)
                    .setContentTitle(remoteMessage.notification?.title)
                    .setContentText(remoteMessage.notification?.body)
                    //.setLargeIcon(smallIcon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(largeIcon)
                        .bigLargeIcon(smallIcon)
                    )
                    .setContentIntent(pendingIntent)

                val notificationManager = NotificationManagerCompat.from(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Default channel",
                        NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(1, builder.build())
            }
        }

        if (remoteMessage?.notification?.imageUrl == null
            && remoteMessage?.notification?.icon != null && remoteMessage.notification != null) {
            remoteMessage.notification?.let {
                Log.d(TAG, "33333")
                val builder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(resId)
                    .setContentTitle(remoteMessage.notification?.title)
                    .setContentText(remoteMessage.notification?.body)
                    .setLargeIcon(smallIcon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)

                val notificationManager = NotificationManagerCompat.from(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Default channel",
                        NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(1, builder.build())
            }
        }

        if (remoteMessage?.notification?.imageUrl == null
            && remoteMessage?.notification?.icon == null
            && remoteMessage?.notification != null && remoteMessage.data != null) {

            remoteMessage.notification?.let {
                Log.d(TAG, "4444444")
                val builder = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(resId)
                    .setContentTitle(remoteMessage.notification?.title)
                    .setContentText(remoteMessage.notification?.body)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)

                val notificationManager = NotificationManagerCompat.from(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Default channel",
                        NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }
                notificationManager.notify(1, builder.build())
            }
        }*/

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
            //val icon = R.mipmap.ic_launcher
            //val title = notificationData["title"]
            val title = notification.title
            //val text = notificationData["text"]
            val text = notification.body
            Log.d(TAG, "onMessageReceived: title $title")
            /*var channelId = notificationData["channel_id"]
            if (channelId == null) {
                channelId = "NOTIFICATION_CHANNEL"
            }*/

            //val intent = Intent(this, MainActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            /*val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                0)*/
            //val largeIcon = remoteMessage.notification?.imageUrl.let { getBitmapFromUrl(it.toString()) }
            //val smallIcon = remoteMessage.notification?.icon.let { getBitmapFromUrl(it)}

            val imageUrl = remoteMessage.notification?.icon.toString()
            val smallIcon = getBitmapFromUrl(imageUrl)
            if (remoteMessage.notification?.icon != null) {
                getBitmapFromUrl(imageUrl)
            }
            //Log.d(TAG, "onMessageReceived: smallIcon $smallIcon")
            val notificationManager = NotificationManagerCompat.from(this)
            val notificationB =
                NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(resId)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setLargeIcon(smallIcon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    //.setColor(this.resources.getColor(R.color.black, null))
                    .build()
            notificationManager.notify(1, notificationB)
        }
    }

    private fun getBitmapFromUrl(imageUrl: String?): Bitmap? {
        /* var b: Bitmap? = null
         try {
             if (imageUrl?.let { isUrlValid(it) } == true) {
                 val url = URL(imageUrl)
                 val connection = url.openConnection() as HttpURLConnection
                 connection.doInput = true
                 connection.connect()
                 val input = connection.inputStream
                 //return BitmapFactory.decodeStream(input)
                 b = BitmapFactory.decodeStream(input)
             }
         } catch (e: IOException) {
             throw RuntimeException(e)
         }*/
        /*var b: Bitmap? = null
        if (imageUrl?.let { isUrlValid(it) } == true) {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            b = BitmapFactory.decodeStream(input)
        }*/
        /*val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        val b = BitmapFactory.decodeStream(input)
        return b//BitmapFactory.decodeStream(input)*/

        var bit: Bitmap? = null
        object : Thread() {
            override fun run() {
                try {
                    if (imageUrl?.let { isUrlValid(it) } == true) {
                        val url = URL(imageUrl)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.connect()
                        val input = connection.inputStream
                        b = BitmapFactory.decodeStream(input)
                        //bit = b
                        Log.d(TAG, "getBitmapFromUrl: b $b")
                    }
                } catch (e: IOException) {
                    Log.d(TAG, "getBitmapFromUrl: e $e")
                    //throw RuntimeException(e)
                }
            }
        }.start()
        Log.d(TAG, "getBitmapFromUrl: bit $bit")
        return b
    }

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

    private fun isUrlValid(url: String): Boolean {
        return try {
            val obj = URL(url)
            obj.toURI()
            true
        } catch (e: MalformedURLException) {
            Log.d(TAG, "isUrlValid: MalformedURLException $e")
            false
        } catch (e: URISyntaxException) {
            Log.d(TAG, "isUrlValid: URISyntaxException $e")
            false
        }
    }
}