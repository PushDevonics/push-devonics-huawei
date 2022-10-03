package pro.devonics.push

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import pro.devonics.push.model.PushData
import pro.devonics.push.model.TimeData
import pro.devonics.push.network.ApiHelper
import pro.devonics.push.network.RetrofitBuilder
import java.util.*

private const val TAG = "PushDevonics"
private const val PERMISSIONS_REQUEST_CODE = 2

class PushDevonics(activity: Activity, appId: String) : LifecycleEventObserver {

    private val service = ApiHelper(RetrofitBuilder.apiService)
    private val helperCache = HelperCache(activity)
    private val myContext = activity

    init {
        AppContextKeeper.setContext(activity)
        PushInit.run(appId, service)
        DataHelper.startTime()
        startSession()
        createInternalId()

    }
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        //Log.d(TAG, "onStateChanged: source = $source")
        when (event) {
            Lifecycle.Event.ON_CREATE -> askNotificationPermission()//Log.d(TAG, "ON_CREATE: ")
            Lifecycle.Event.ON_START -> Log.d(TAG, "ON_START: ")
            Lifecycle.Event.ON_RESUME -> {
                Log.d(TAG, "ON_RESUME: ")
                //sendTransition()
                openUrl(myContext)
            }
            //Lifecycle.Event.ON_RESUME -> Log.d(TAG, "onResume: ")
            Lifecycle.Event.ON_PAUSE -> Log.d(TAG, "onPause: ")
            Lifecycle.Event.ON_STOP -> stopSession()
            //Lifecycle.Event.ON_STOP -> Log.d(TAG, "onStop: ")
            Lifecycle.Event.ON_DESTROY -> Log.d(TAG, "onDestroy: ")
            else -> {}
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    myContext,
                    POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(myContext, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    myContext,
                    POST_NOTIFICATIONS
                )
            ) {
                Toast.makeText(myContext, "Require Permission for notification", Toast.LENGTH_SHORT)
                    .show()
                Log.v(TAG, "askNotificationPermission: ")
            } else {
                //requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                myContext.requestPermissions(
                    arrayOf(POST_NOTIFICATIONS),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    fun getIntentData(intent: Intent?) {

        if (null != intent) {
            val bundle = intent.extras

            val sentPushId = bundle?.getString("sent_push_id")
            if (sentPushId != null) {
                val pushData = PushData(sentPushId)
                val pushCache = PushCache()
                val registrationId = pushCache.getRegistrationIdFromPref()
                /*if (registrationId != null) {
                    service.createTransition(registrationId, pushData)
                }*/
                sendTransition()
                Log.d(TAG, "getIntentData: sentPushId $sentPushId")
            }
        } else {
            Log.i(TAG, "intent = null")
        }
    }

    private fun sendTransition() {
        val sentPushId = helperCache.getSentPushId()
        val pushCache = PushCache()
        val registrationId = pushCache.getRegistrationIdFromPref()
        if (sentPushId != null) {
            val pushData = PushData(sentPushId)
            if (registrationId != null) {
                service.createTransition(registrationId, pushData)
            }
            Log.d(TAG, "sendTransition: pushData = $pushData")
        }
        helperCache.saveSentPushId(null)
    }

    private fun openUrl(context: Context) {
        val openUrl = helperCache.getOpenUrl()
        //val sentPushId = helperCache.getSentPushId()
        Log.d(TAG, "openUrl = $openUrl")
        if (openUrl != null) {
            val urlIntent = Intent()
                .setAction(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(openUrl))

            urlIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(urlIntent)
                sendTransition()
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "ActivityNotFoundException $e")
            }
        }
        helperCache.saveOpenUrl(null)
        Log.d(TAG, "openUrl = $openUrl")
    }

    fun getDeeplink(): String {
        val deep1 = helperCache.getDeeplink()
        helperCache.saveDeeplink("")
        return deep1.toString()
    }

    fun createInternalId() {
        val pushCache = PushCache()

        var internalId = pushCache.getInternalIdFromPref()
        //var internalId = getInternalId()
        if (internalId == null) {
            internalId = UUID.randomUUID().toString()
            //val uuid = UUID.randomUUID()
            //internalId = uuid.toString()
            pushCache.saveInternalId(internalId)

        }
        Log.d(TAG, "createInternalId: internalId $internalId")
    }

    fun getInternalId(): String? {
        val pushCache = PushCache()
        return pushCache.getInternalIdFromPref()
    }

    private fun startSession() {
        Log.d(TAG, "startSession: ")
        val pushCache = PushCache()
        val registrationId = pushCache.getRegistrationIdFromPref()
        if (pushCache.getSubscribeStatusFromPref() == true) {
            val session = registrationId?.let { service.createSession(it) }
            //Log.d(TAG, "subscribeStatus = ${pushCache.getSubscribeStatusFromPref()}")

        }
    }

    private fun stopSession() {
        val duration = DataHelper.getDuration()
        val pushCache = PushCache()
        val regId = pushCache.getRegistrationIdFromPref()
        if (regId != null) {
            val timeData = TimeData(duration)
            service.sendTimeStatistic(regId, timeData)
            //Log.d(TAG, "stopSession: timeData $timeData")
        }

        //Log.d(TAG, "stopSession: duration $duration")
        //Log.d(TAG, "stopSession: regId $regId")
        Log.d(TAG, "stopSession")
    }

    fun setTags(key: String, value: String) {
        val pushCache = PushCache()
        if (key == null && value == null) {
            pushCache.saveTagKey("")
            pushCache.saveTagValue("")
        } else {
            pushCache.saveTagKey(key)
            pushCache.saveTagValue(value)
        }
    }
}