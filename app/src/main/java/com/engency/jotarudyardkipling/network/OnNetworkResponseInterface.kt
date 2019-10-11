package com.engency.jotarudyardkipling.network

import org.json.JSONObject

interface OnNetworkResponseInterface {
    fun success(data : JSONObject)
    fun failure(message : String)
}