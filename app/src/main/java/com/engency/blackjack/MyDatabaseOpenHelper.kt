package com.engency.blackjack

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.engency.blackjack.Models.GroupProperty
import org.jetbrains.anko.db.*

class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("User", true)
    }
}
