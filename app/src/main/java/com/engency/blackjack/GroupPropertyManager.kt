package com.engency.blackjack

import android.content.Context
import com.engency.blackjack.Models.GroupProperty
import org.jetbrains.anko.db.*

class GroupPropertyManager {

    private var context: Context
    private var database: MyDatabaseOpenHelper
    private var properties: HashMap<String, String> = HashMap()
    private var hasChanged: Boolean = false

    companion object {
        private var instance: GroupPropertyManager? = null

        @Synchronized
        fun getInstance(ctx: Context): GroupPropertyManager {
            if (instance == null) {
                instance = GroupPropertyManager(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    private constructor(context: Context) {
        this.context = context
        this.database = MyDatabaseOpenHelper.getInstance(this.context)

        parse()
    }

    private fun parse() {
        val properties = this.database.use {
            select(GroupProperty.TABLE_NAME, GroupProperty.COLUMN_KEY, GroupProperty.COLUMN_VALUE).exec {
                parseList(classParser<GroupProperty>())
            }
        }

        for (property in properties) {
            this.properties.put(property.key, property.value)
        }
    }

    fun has(key: String): Boolean {
        return this.properties.containsKey(key)
    }

    fun get(key: String): String? {
        return this.properties.get(key)
    }

    fun put(key: String, value: String) {
        val original = this.get(key)
        if (original == value) {
            return
        }

        this.properties.put(key, value)
        this.hasChanged = true
    }

    fun clear() {
        this.properties.clear()
        this.hasChanged = true
        this.commit()
    }

    fun commit() {
        this.database.use {
            delete(GroupProperty.TABLE_NAME)

            properties.forEach { key, value ->
                insert(GroupProperty.TABLE_NAME,
                        GroupProperty.COLUMN_KEY to key,
                        GroupProperty.COLUMN_VALUE to value
                )
            }
        }
    }
}