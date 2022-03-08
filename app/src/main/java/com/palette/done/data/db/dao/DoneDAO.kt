package com.palette.done.data.db.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.palette.done.data.db.entity.Done
import com.palette.done.data.db.entity.Plan
import com.palette.done.data.db.entity.Routine
import com.palette.done.data.db.entity.TodayRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DoneDAO {
    // 던 ------------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDone(done: Done)

    @Delete
    suspend fun deleteDone(done: Done)

    @Query("SELECT * FROM Done WHERE date = :date ORDER BY doneId ASC")
    fun getAllDoneInDate(date: String): Flow<List<Done>>

    @Query("SELECT * FROM Done")
    fun getAllDone(): Flow<List<Done>>

    @Query("SELECT COUNT(*) FROM Done WHERE date LIKE :yearMonth")
    fun getAllDoneCountInMonth(yearMonth: String): Int

    @Query("SELECT COUNT(*) FROM Done WHERE date = :date")
    suspend fun getAllDoneCountInDate(date: String): Int

    // 플랜 -----------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: Plan)

    @Query("DELETE FROM `Plan` WHERE planNo = :planNo")
    suspend fun deletePlan(planNo: Int)

    @Query("SELECT * FROM `Plan` ORDER BY planNo")
    fun getAllPlan(): Flow<List<Plan>>

    // 루틴 -----------------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine)

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Query("SELECT * FROM Routine ORDER BY routineNo")
    fun getAllRoutine(): Flow<List<Routine>>

    // 오늘 한마디 -----------------------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodayRecord(todayRecord: TodayRecord)

    @Delete
    suspend fun deleteTodayRecord(todayRecord: TodayRecord)

    @Query("SELECT * FROM TodayRecord WHERE date = :date")
    fun getTodayRecord(date: String): Flow<TodayRecord>
}