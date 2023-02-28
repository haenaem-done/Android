package com.palette.done.data.db.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class IntSetTypeConverter(private val gson: Gson) {

    @TypeConverter
    fun setToJson(value: Set<Int>): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToSet(value: String): Set<Int> {
        return gson.fromJson(value, Array<Int>::class.java).toSet()
    }
}