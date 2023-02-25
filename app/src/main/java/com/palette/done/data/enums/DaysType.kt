package com.palette.done.data.enums

enum class DaysType(val idx: Int, val kor: String) {
    MON(0, "월"), TUE(1, "화"), WED(2, "수"),
    THR(3, "목"), FRI(4, "금"), SAT(5, "토"), SUN(6, "일");

    companion object {
        fun valueOf(value: Int): DaysType {
            return values().find {
                it.idx == value
            } ?: MON
        }
    }
}