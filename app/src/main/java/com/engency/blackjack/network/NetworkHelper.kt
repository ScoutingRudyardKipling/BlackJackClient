package com.engency.blackjack.network

import android.util.Log
import com.engency.blackjack.BuildConfig
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.gson.responseObject

data class LoginResponse(var groupInfo: GroupInfo, var token: String = "")
data class GroupInfo(var name: String, var group: String, var points: Number, var products: List<ServerProduct>)
data class ServerTeamScore(var _id: Int, val name: String, val group: String, val points: Int)
data class ServerProduct(var _id: Int, val name: String, val description: String, val image: String, val costs: Int, val reward: Int, val code: String, val rewarded: Boolean)
data class GroupsResponse(var scores: List<ServerTeamScore>);
data class BarcodeSuccess(var groupInfo: GroupInfo, var type: String, var message: String, var productId: Int)

class NetworkHelper {
    companion object {

        fun login(groupName: String, password: String, handler: OnNetworkResponseInterface<LoginResponse>) {
            Fuel.post("${BuildConfig.SERVER_URL}/auth/login", listOf("name" to groupName, "password" to password))
                    .responseObject<LoginResponse> { _, _, result ->
                        result.fold(
                                success = { data: LoginResponse -> handler.success(data) },
                                failure = { error: FuelError -> this.handleFuelError(error, handler) }
                        )
                    }
        }

        fun submitProduct(code: String, token: String, success: (BarcodeSuccess) -> Unit, failure: (String) -> Unit) {
            Fuel.post("${BuildConfig.SERVER_URL}/products", listOf("code" to code))
                    .header("x-token" to token)
                    .responseObject<BarcodeSuccess> { _, _, result ->
                        result.fold(
                                success = success,
                                failure = { error: FuelError -> this.handleFuelError(error, failure) }
                        )
                    }
        }

        fun submitFCMToken(token: String, fcmToken: String, success: (Any) -> Unit, failure: (String) -> Unit) {
            Fuel.post("${BuildConfig.SERVER_URL}/groups/current/fcm", listOf("token" to fcmToken))
                    .header("x-token" to token)
                    .responseObject<Any> { _, _, result ->
                        result.fold(
                                success = success,
                                failure = { error: FuelError -> this.handleFuelError(error, failure) }
                        )
                    }
        }

        fun getGroupInfo(token: String, success: (GroupInfo) -> Any, failure: (String) -> Unit) {
            Fuel.get("${BuildConfig.SERVER_URL}/groups/current")
                    .header("x-token" to token)
                    .responseObject<GroupInfo> { _, _, result ->
                        result.fold(
                                success = success,
                                failure = { error: FuelError -> this.handleFuelError(error, failure) }
                        )
                    }
        }

        fun listScores(token: String, success: (GroupsResponse) -> Any, failure: (String) -> Unit) {
            Fuel.get("${BuildConfig.SERVER_URL}/groups")
                    .header("x-token" to token)
                    .responseObject<GroupsResponse> { _, _, result ->
                        result.fold(
                                success = success,
                                failure = { error: FuelError -> this.handleFuelError(error, failure) }
                        )
                    }
        }

        private fun handleFuelError(error: FuelError, callback: (String) -> Unit) {
            callback(extractMessage(error))
        }

        private fun handleFuelError(error: FuelError, callback: OnNetworkResponseInterface<*>) {
            callback.failure(extractMessage(error))
        }

        private fun extractMessage(error: FuelError): String {
            val jsonData = Json(String(error.response.data)).obj()

            var message = "Unknown error"
            if (jsonData.has("message")) {
                message = jsonData.getString("message")
            }

            Log.e("Fuel error", "$message - $error")

            return message;
        }
    }
}
