package com.palette.done.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.palette.done.data.enums.DaysType
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Alarm(
    @PrimaryKey
    var alarmNo: Int = 1,
    var hour: Int = 0,
    var min: Int = 0,
    var days: Set<Int> = setOf()
) : Parcelable
