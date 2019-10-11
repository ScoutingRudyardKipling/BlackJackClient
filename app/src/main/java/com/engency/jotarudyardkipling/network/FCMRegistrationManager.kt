package com.engency.jotarudyardkipling.network

import android.util.Log
import com.engency.jotarudyardkipling.GroupPropertyManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONObject

class FCMRegistrationManager : OnNetworkResponseInterface {

    private var _token: String = ""
    private lateinit var properties: GroupPropertyManager

    fun register(token: String, gp : GroupPropertyManager) {
        this._token = token
        this.properties = gp
        this.getFirebaseId()
    }

    private fun getFirebaseId() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("FCM", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result!!.token
                    NetworkHelper.submitFCMToken(this._token, token, this)
                })
    }

    override fun success(data: JSONObject) {
        properties.put("registered", "1")
        properties.commit()
    }

    override fun failure(message: String) {
        if (message == "Token wordt al gebruikt") {
            properties.put("registered", "1")
            properties.commit()
        }
    }

}