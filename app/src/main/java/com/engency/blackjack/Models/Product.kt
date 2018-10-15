package com.engency.blackjack.Models

import org.json.JSONObject

data class Product(var id: Int, val name: String, val image: String, val costs: Int, val reward: Int, val code: String, val bought: Boolean, val rewarded: Boolean) {
    companion object {
        val TABLE_NAME = "Product"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_IMAGE = "image"
        val COLUMN_COSTS = "costs"
        val COLUMN_REWARD = "reward"
        val COLUMN_CODE = "code"
        val COLUMN_BOUGHT = "bought"
        val COLUMN_REWARDED = "rewarded"

        fun fromJsonObject(item: JSONObject): Product {
            return Product(
                    item.getInt("_id"),
                    item.getString("name"),
                    item.getString("image"),
                    item.getInt("reward"),
                    item.getInt("costs"),
                    item.getString("code"),
                    item.getBoolean("bought"),
                    item.getBoolean("rewarded")
            )
        }

        fun fromMap(item: Map<String, String>): Product {
            return Product(
                    item.getValue("_id").toInt(),
                    item.getValue("name"),
                    item.getValue("image"),
                    item.getValue("reward").toInt(),
                    item.getValue("costs").toInt(),
                    item.getValue("code"),
                    item.getValue("bought") == "true",
                    item.getValue("rewarded") == "true"
            )
        }
    }
}