package com.engency.blackjack.network

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import org.json.JSONObject


class NetworkHelper {
    companion object {

        private var baseUrl = "https://blackjack.engency.com:3000"

        fun login(groupName: String, password: String, handler: OnNetworkResponseInterface) {
            post(
                    "auth/login",
                    "",
                    listOf("name" to groupName, "password" to password),
                    handler
            )
        }

        fun submitProduct(code: String, token: String, handler: OnNetworkResponseInterface) {
            post("products", token, listOf("code" to code), handler)
        }

        fun getGroupInfo(token: String, handler: OnNetworkResponseInterface) {
            get("groups/current", token, handler)
        }

        fun unlock(token: String, productId: Int, handler: OnNetworkResponseInterface) {
            put(
                    "products/$productId",
                    token,
                    listOf("action" to "unlock"),
                    handler
            )
        }

        private fun get(path: String, token: String, handler: OnNetworkResponseInterface) {
            Fuel.get("$baseUrl/$path")
                    .header("x-token" to token)
                    .responseJson { _, resp, result -> handleResponse(resp, result, handler) }
        }

        private fun put(path: String, token: String, parameters: List<Pair<String, Any?>>? = null, handler: OnNetworkResponseInterface) {
            Fuel.put("$baseUrl/$path", parameters)
                    .header("x-token" to token)
                    .responseJson { _, resp, result -> handleResponse(resp, result, handler) }
        }

        private fun patch(path: String, token: String, parameters: List<Pair<String, Any?>>? = null, handler: OnNetworkResponseInterface) {
            Fuel.patch("$baseUrl/$path", parameters)
                    .header("x-token" to token)
                    .responseJson { _, resp, result -> handleResponse(resp, result, handler) }
        }

        private fun post(path: String, token: String, parameters: List<Pair<String, Any?>>? = null, handler: OnNetworkResponseInterface) {
            Fuel.post("$baseUrl/$path", parameters)
                    .header("x-token" to token)
                    .responseJson { _, resp, result -> handleResponse(resp, result, handler) }
        }

        private fun handleResponse(resp: Response, result: Result<Json, FuelError>, handler: OnNetworkResponseInterface) {
            result.fold(success = { json ->
                val data: JSONObject = json.obj()
                val success: Boolean = data.getBoolean("success")

                if (success) {
                    handler.success(data.getJSONObject("data"))
                } else {
                    handler.failure(data.getString("message"))
                }
            }, failure = { error ->
                Log.e("qdp error", error.toString())
                handler.failure("Er trad een onbekende fout op")
            })
        }
    }
}