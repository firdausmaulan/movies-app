package com.movies.app.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.movies.app.util.AppLog

class DatabaseManager(context: Context?) :
        SQLiteOpenHelper(context, "db_movie_app", null, 1) {

    private val T_FAVOURITE = "t_favourite"

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE $T_FAVOURITE (id INTEGER, json_data TEXT)")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }

    fun addFavourite(id: Int?, jsonData: String) {
        val db = writableDatabase
        var contentValues: ContentValues? = null
        contentValues = ContentValues()
        contentValues.put("id", id)
        contentValues.put("json_data", jsonData)
        db.insert(T_FAVOURITE, null, contentValues)
        db.close()
    }

    fun removeFavourite(id: Int?) {
        val db = writableDatabase
        db.delete(T_FAVOURITE, "id=$id", null)
        db.close()
    }

    fun getJsonFavourite(): String {
        var json = "["
        val db = readableDatabase
        val c = db.rawQuery("SELECT json_data FROM $T_FAVOURITE", arrayOf())
        c.moveToFirst()
        while (!c.isAfterLast) {
            json = json + c.getString(0) + ","
            c.moveToNext()
        }
        if (json.contains(",".toRegex())) json = json.substring(0, json.length - 1)
        json = "$json]"
        c.close()
        db.close()
        AppLog.d(json)
        return json
    }

    fun getListFavouriteID(): ArrayList<Int> {
        val list = ArrayList<Int>()
        val db = readableDatabase
        val c = db.rawQuery("SELECT id FROM $T_FAVOURITE", arrayOf())
        c.moveToFirst()
        while (!c.isAfterLast) {
            list.add(c.getInt(0))
            c.moveToNext()
        }
        c.close()
        db.close()
        return list
    }

    fun isFavourite(movieId: Int?): Boolean {
        val db = readableDatabase
        val c = db.rawQuery("SELECT id FROM $T_FAVOURITE WHERE id=$movieId", arrayOf())
        val isFavourite = c.count > 0
        c.close()
        db.close()
        return isFavourite
    }
}
