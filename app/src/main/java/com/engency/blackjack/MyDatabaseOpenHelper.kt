package com.engency.blackjack

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.engency.blackjack.Models.GroupProperty
import com.engency.blackjack.Models.Product
import com.engency.blackjack.Models.TeamScore
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 2) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(GroupProperty.TABLE_NAME, true,
                GroupProperty.COLUMN_KEY to TEXT + PRIMARY_KEY + UNIQUE,
                GroupProperty.COLUMN_VALUE to TEXT)

        db.createTable(Product.TABLE_NAME, true,
                Product.COLUMN_ID to INTEGER + PRIMARY_KEY + UNIQUE,
                Product.COLUMN_NAME to TEXT,
                Product.COLUMN_DESCRIPTION to TEXT,
                Product.COLUMN_IMAGE to TEXT,
                Product.COLUMN_COSTS to INTEGER,
                Product.COLUMN_REWARD to INTEGER,
                Product.COLUMN_CODE to TEXT,
                Product.COLUMN_REWARDED to INTEGER
        )

        db.createTable(TeamScore.TABLE_NAME, true,
                TeamScore.COLUMN_ID to INTEGER + PRIMARY_KEY + UNIQUE,
                TeamScore.COLUMN_NAME to TEXT,
                TeamScore.COLUMN_GROUP to TEXT,
                TeamScore.COLUMN_SCORE to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // no upgrade available
        db.execSQL("DROP TABLE ${Product.TABLE_NAME};")

        db.createTable(Product.TABLE_NAME, true,
                Product.COLUMN_ID to INTEGER + PRIMARY_KEY + UNIQUE,
                Product.COLUMN_NAME to TEXT,
                Product.COLUMN_DESCRIPTION to TEXT,
                Product.COLUMN_IMAGE to TEXT,
                Product.COLUMN_COSTS to INTEGER,
                Product.COLUMN_REWARD to INTEGER,
                Product.COLUMN_CODE to TEXT,
                Product.COLUMN_REWARDED to INTEGER
        )
    }
}
