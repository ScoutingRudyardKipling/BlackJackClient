package com.engency.blackjack.Models

import com.engency.blackjack.network.ServerTeamScore

data class TeamScore(var id: Int, val name: String, val group: String, val score: Int) {

    var index: Int = 0

    companion object {
        val TABLE_NAME = "TeamScore"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_GROUP = "groupName"
        val COLUMN_SCORE = "score"

        fun fromServerTeamScore(item: ServerTeamScore): TeamScore {
            return TeamScore(item._id, item.name, item.group, item.points);
        }
    }
}