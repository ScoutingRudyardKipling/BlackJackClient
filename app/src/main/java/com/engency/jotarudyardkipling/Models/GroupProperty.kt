package com.engency.jotarudyardkipling.Models

data class GroupProperty(val key: String, val value: String) {
    companion object {
//        val GroupProperty.COLUMN_ID = "key"
        val TABLE_NAME = "GroupProperties"
        val COLUMN_KEY = "key"
        val COLUMN_VALUE = "value"
    }
}