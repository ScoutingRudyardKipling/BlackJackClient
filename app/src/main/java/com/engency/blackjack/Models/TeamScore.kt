package com.engency.blackjack.Models

import org.json.JSONObject

data class TeamScore(var id: Int, val name: String, val group: String, val score: Int) {
    companion object {
        val TABLE_NAME = "TeamScore"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_GROUP = "groupName"
        val COLUMN_SCORE = "score"

        fun fromJsonObject(item: JSONObject): TeamScore {
            return TeamScore(
                    item.getInt("_id"),
                    item.getString("name"),
                    item.getString("group"),
                    item.getInt("points")
            )
        }
    }
}