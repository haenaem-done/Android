package com.palette.done.data.enums

import java.util.Calendar

enum class DaysType(val idx: Int, val kor: String) {
    MON(Calendar.MONDAY, "월"), TUE(Calendar.TUESDAY, "화"), WED(Calendar.WEDNESDAY, "수"),
    THR(Calendar.THURSDAY, "목"), FRI(Calendar.FRIDAY, "금"), SAT(Calendar.SATURDAY, "토"),
    SUN(Calendar.SUNDAY, "일"), NONE(-1, "없음");

    companion object {
        fun valueOf(value: Int): DaysType {
            return values().find {
                it.idx == value
            } ?: NONE
        }
    }
}