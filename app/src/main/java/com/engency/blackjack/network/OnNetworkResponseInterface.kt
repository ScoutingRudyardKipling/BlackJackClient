package com.engency.blackjack.network

import org.json.JSONObject

interface OnNetworkResponseInterface {
    fun success(data : JSONObject)
    fun failure(message : String)
}