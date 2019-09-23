package com.engency.blackjack.stores

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.engency.blackjack.Models.Product
import com.engency.blackjack.MyDatabaseOpenHelper
import org.jetbrains.anko.db.*
import org.json.JSONArray
import org.json.JSONObject

class ProductStore(private var ctx: Context) {

    private var database: MyDatabaseOpenHelper = MyDatabaseOpenHelper.getInstance(this.ctx)


    fun getAll(): List<Product> {
        return this.database.use {
            select(Product.TABLE_NAME).exec {
                parseList(classParser())
            }
        }
    }

    fun getAllSorted(): List<Product> {
        return getAll().sortedWith(compareBy { it.name })
    }

    fun getById(id: Int): Product? {
        return this.database.use {
            select(Product.TABLE_NAME).whereArgs(Product.COLUMN_ID + " = {id}", "id" to id).exec {
                parseSingle(classParser())
            }
        }
    }

    fun update(product: Product) {
        return this.database.use {
            update(Product.TABLE_NAME,
                    Product.COLUMN_NAME to product.name,
                    Product.COLUMN_DESCRIPTION to product.description,
                    Product.COLUMN_IMAGE to product.image,
                    Product.COLUMN_COSTS to product.costs,
                    Product.COLUMN_REWARD to product.reward,
                    Product.COLUMN_CODE to product.code,
                    Product.COLUMN_REWARDED to product.rewarded
            ).whereArgs(Product.COLUMN_ID + " = {id}", "id" to product.id).exec()
        }
    }

    fun clear() {
        this.database.use {
            execSQL("delete from " + Product.TABLE_NAME)
        }
    }

    fun add(product: Product) {
        this.database.use {

            // create database item
            val values = ContentValues()
            values.put(Product.COLUMN_ID, product.id)
            values.put(Product.COLUMN_NAME, product.name)
            values.put(Product.COLUMN_DESCRIPTION, product.description)
            values.put(Product.COLUMN_IMAGE, product.image)
            values.put(Product.COLUMN_COSTS, product.costs)
            values.put(Product.COLUMN_REWARD, product.reward)
            values.put(Product.COLUMN_CODE, product.code)
            values.put(Product.COLUMN_REWARDED, product.rewarded)

            // insert
            insert(Product.TABLE_NAME, null, values)
        }
    }

    fun add(product: JSONObject) {
        add(Product.fromJsonObject(product))
    }

    fun addAll(products: List<Product>) {
        for (product in products) {
            add(product)
        }
    }

    fun addAll(products: JSONArray) {
        for (i in 0 until products.length()) {
            add(products.getJSONObject(i))
        }
    }

    fun hasProductWithCode(code: String): Boolean {
        return this.database.use {
            select(Product.TABLE_NAME).whereArgs(Product.COLUMN_CODE + " = {code}", "code" to code).exec {
                count > 0
            }
        }
    }

}