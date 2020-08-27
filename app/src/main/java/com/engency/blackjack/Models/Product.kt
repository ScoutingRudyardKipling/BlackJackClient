package com.engency.blackjack.Models

import com.engency.blackjack.network.ServerProduct

data class Product(var id: Int, val name: String, val description: String, val image: String, val costs: Int, val reward: Int, val code: String, val rewarded: Boolean) {
    companion object {
        val TABLE_NAME = "Product"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_DESCRIPTION = "description"
        val COLUMN_IMAGE = "image"
        val COLUMN_COSTS = "costs"
        val COLUMN_REWARD = "reward"
        val COLUMN_CODE = "code"
        val COLUMN_REWARDED = "rewarded"

        fun fromServerProduct(item: ServerProduct): Product {
            return Product(item._id, item.name, item.description, item.image, item.costs, item.reward, item.code, item.rewarded)
        }

        fun fromMap(item: Map<String, String>): Product {
            return Product(
                    item.getValue("_id").toInt(),
                    item.getValue("name"),
                    item.getValue("description"),
                    item.getValue("image"),
                    item.getValue("costs").toInt(),
                    item.getValue("reward").toInt(),
                    item.getValue("code"),
                    item.getValue("rewarded") == "true"
            )
        }
    }
}