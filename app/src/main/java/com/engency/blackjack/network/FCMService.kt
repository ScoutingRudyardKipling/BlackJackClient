package com.engency.blackjack.network

import android.content.Intent
import android.util.Log
import com.engency.blackjack.GroupPropertyManager
import com.engency.blackjack.Models.Product
import com.engency.blackjack.stores.ProductStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FCMService : FirebaseMessagingService() {

    override fun onNewToken(mToken: String) {
        super.onNewToken(mToken)

        val groupProperties = GroupPropertyManager(this)
        groupProperties.put("fcmtoken", mToken)
        groupProperties.commit()

        // Submit new Instance ID token
        FCMRegistrationManager().storeFirebaseId(groupProperties)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty() && remoteMessage.data.containsKey("event")) {
            when (remoteMessage.data.getValue("event")) {
                "new-product" -> newProduct(Product.fromMap(remoteMessage.data.toMap()))
                "update-product" -> updateProduct(Product.fromMap(remoteMessage.data.toMap()))
                "update-property" -> updateProperty(remoteMessage.data.getValue("property"), remoteMessage.data.getValue("value"))
            }
        }

    }

    fun newProduct(product: Product) {
        Log.d("FCM CREATE PRODUCT", product.name)

        // get productStore
        val productStore = ProductStore(this)
        productStore.add(product)

        sendBroadcast(Intent ("refresh"))
    }

    fun updateProduct(product: Product) {
        Log.d("FCM UPDATE PRODUCT", product.name)

        // get productStore
        val productStore = ProductStore(this)
        productStore.update(product)

        sendBroadcast(Intent ("refresh"))
    }

    fun updateProperty(property: String, value: String) {
        Log.d("FCM UPDATE PROP", "PROPERTY " + property + " to " + value)

        // get GroupPropertyManager
        val gpm = GroupPropertyManager(this)
        gpm.put(property, value)
        gpm.commit()

        sendBroadcast(Intent ("refresh"))
    }
}