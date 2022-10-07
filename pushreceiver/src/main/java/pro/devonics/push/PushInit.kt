package pro.devonics.push

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import pro.devonics.push.model.PushUser
import pro.devonics.push.network.ApiHelper
import java.util.*

private const val TAG = "PushInit"

class PushInit {

    companion object {

        private val appContext = AppContextKeeper.getContext()

        fun run(appId: String, service: ApiHelper) {
            val pushCache = PushCache()
            object : Thread() {
                override fun run() {
                    try {
                        //Log.d(TAG, "run() appId: $appId")
                        val registrationId = HmsInstanceId.getInstance(appContext)
                            .getToken(appId, "HCM")
                        Log.d(TAG, "run() registrationId: $registrationId")
                        //val regId = pushCache.getRegistrationIdFromPref()
                        val internalId = pushCache.getInternalIdFromPref()
                        val status = pushCache.getSubscribeStatusFromPref()
                        if (registrationId != null) {
                            pushCache.saveRegistrationIdPref(registrationId)
                            if (status == false) {
                                val pushUser = internalId?.let {
                                    setPushUser(
                                        registrationId, appId, appContext, it
                                    )
                                }
                                pushUser?.let { service.createPush(it, appId) }
                            }
                        }
                        //Log.d(TAG, "getToken() token: $registrationId")
                    } catch (e: ApiException) {
                        Log.e("PUSH", "getToken() failure: ${e.message}")
                    }
                }
            }.start()
        }

        fun setPushUser(
            registrationId: String,
            appId: String,
            appContext: Context,
            internalId: String): PushUser {

            //Get timezone
            val tz = TimeZone.getDefault()//.toZoneId()
            val timezone = tz.id
            //Log.d(TAG, "complete: timezone = $timezone")

            //Get language
            val locale = Locale.getDefault()
            val lang = locale.language
            //Log.d(TAG, "complete: lang = $lang")

            //Get country
            val telephonyManager = appContext
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val country = telephonyManager.simCountryIso.uppercase(Locale.getDefault())
            //val country = Locale("", locale.country).country
            //.getDisplayCountry(Locale("EN"))
            //Log.d(TAG, "complete: country = $country")


            //Get device info
            val deviceInfo = getDeviceData()
            //Log.d(TAG, "complete: deviceInfo = $deviceInfo")

            return PushUser(
                registrationId,
                internalId,
                appId,
                4,
                country,
                lang,
                timezone,
                deviceInfo
            )
        }

        private fun getDeviceData(): String {
            //val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val brand = Build.BRAND
            //val product = Build.PRODUCT

            return "$brand/$model"
        }
    }
}