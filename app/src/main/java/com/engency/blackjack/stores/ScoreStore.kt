package com.engency.blackjack.stores

import android.content.ContentValues
import android.content.Context
import com.engency.blackjack.Models.TeamScore
import com.engency.blackjack.MyDatabaseOpenHelper
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.select
import org.json.JSONArray
import org.json.JSONObject

class ScoreStore(private var ctx: Context) {

    private var database: MyDatabaseOpenHelper = MyDatabaseOpenHelper.getInstance(this.ctx)


    fun getAll(): List<TeamScore> {
        return this.database.use {
            select(TeamScore.TABLE_NAME).exec {
                parseList(classParser())
            }
        }
    }

    fun getAllSorted(): List<TeamScore> {
        var index = 1
        val list = getAll().sortedWith(compareByDescending { it.score })

        list.forEach {
            it.index = index++
        }

        return list
    }

    fun clear() {
        this.database.use {
            execSQL("delete from " + TeamScore.TABLE_NAME)
        }
    }

    fun add(score: TeamScore) {
        this.database.use {

            // create database item
            val values = ContentValues()
            values.put(TeamScore.COLUMN_ID, score.id)
            values.put(TeamScore.COLUMN_NAME, score.name)
            values.put(TeamScore.COLUMN_GROUP, score.group)
            values.put(TeamScore.COLUMN_SCORE, score.score)

            // insert
            insert(TeamScore.TABLE_NAME, null, values)
        }
    }

    fun add(score: JSONObject) {
        add(TeamScore.fromJsonObject(score))
    }

    fun addAll(scores: List<TeamScore>) {
        for (score in scores) {
            add(score)
        }
    }

    fun addAll(scores: JSONArray) {
        for (i in 0 until scores.length()) {
            add(scores.getJSONObject(i))
        }
    }

}