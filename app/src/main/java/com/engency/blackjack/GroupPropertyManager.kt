package com.engency.blackjack

import android.content.Context
import android.util.Log
import com.engency.blackjack.Models.GroupProperty
import com.engency.blackjack.stores.ProductStore
import org.jetbrains.anko.db.*
import org.json.JSONObject

class GroupPropertyManager(private var context: Context) {

    private var database: MyDatabaseOpenHelper
    private var properties: HashMap<String, String> = HashMap()
    private var hasChanged: Boolean = false

    init {
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

    fun updateWithGroupInstance(data : JSONObject) {
        Log.i("GROUPINFO", data.toString())
        this.put("name", data.getString("name"))
        this.put("group", data.getString("group"))
        this.put("points", data.getInt("points").toString())
        this.put("credits", data.getInt("credits").toString())

        // get productStore
        val productStore = ProductStore(this.context)
        productStore.clear()
        productStore.addAll(data.getJSONArray("products"))


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