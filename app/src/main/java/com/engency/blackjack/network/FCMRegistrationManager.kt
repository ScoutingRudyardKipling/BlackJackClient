package com.engency.blackjack.network

import android.util.Log
import com.engency.blackjack.GroupPropertyManager

class FCMRegistrationManager {

    private lateinit var properties: GroupPropertyManager

    fun storeFirebaseId(gp: GroupPropertyManager) {
        this.properties = gp
        if (!properties.has("token") || !properties.has("fcmtoken")) {
            Log.e("FCM", "Either api- or fcmtoken missing.")
            return
        }

        NetworkHelper.submitFCMToken(properties.get("token")!!, properties.get("fcmtoken")!!,
                success = { properties.put("registered", "1"); properties.commit() },
                failure = { message: String -> this.failure(message) }
        )
    }

    fun failure(message: String) {
        if (message == "Token wordt al gebruikt") {
            properties.put("registered", "1")
            properties.commit()
        }
    }

}