package com.example.util

import android.provider.BaseColumns

object CartReaderContract {
    object CartEntry : BaseColumns {
        const val TABLE_CART = "cart"
        const val COLUMN_NAME_RES_ID = "res_id"
        const val COLUMN_NAME_RES_NAME = "res_name"
        const val COLUMN_NAME_FOOD_QTY = "food_qty"
        const val COLUMN_NAME_CITY_ID = "city_id"

        const val TABLE_FOOD = "food"
        const val COLUMN_NAME_FOOD_ID = "food_id"
        const val COLUMN_NAME_FOOD_NAME = "food_name"
        const val COLUMN_NAME_FOOD_TYPE = "food_id"
        const val COLUMN_NAME_CAT_ID = "cat_id"
        const val COLUMN_NAME_FOOD_PRICE = "food_price"
    }

    const val SQL_CREATE_TABLE_CART =
        "CREATE TABLE ${CartEntry.TABLE_CART} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${CartEntry.COLUMN_NAME_FOOD_ID} TEXT, " +
                "${CartEntry.COLUMN_NAME_FOOD_NAME} TEXT, " +
                "${CartEntry.COLUMN_NAME_FOOD_PRICE} INT, " +
                "${CartEntry.COLUMN_NAME_RES_ID} TEXT, " +
                "${CartEntry.COLUMN_NAME_RES_NAME} TEXT, " +
                "${CartEntry.COLUMN_NAME_CITY_ID} TEXT, " +
                "${CartEntry.COLUMN_NAME_FOOD_QTY} INT DEFAULT 0) "

    const val SQL_CREATE_TABLE_FOOD =
        "CREATE TABLE ${CartEntry.TABLE_FOOD} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${CartEntry.COLUMN_NAME_FOOD_ID} TEXT, " +
                "${CartEntry.COLUMN_NAME_RES_ID} TEXT, " +
                "${CartEntry.COLUMN_NAME_FOOD_PRICE} FLOAT, " +
                "${CartEntry.COLUMN_NAME_CITY_ID} TEXT)"

    const val SQL_DELETE_TABLE_CART = "DROP TABLE IF EXISTS ${CartEntry.TABLE_CART}"
    const val SQL_DELETE_TABLE_FOOD = "DROP TABLE IF EXISTS ${CartEntry.TABLE_FOOD}"
}