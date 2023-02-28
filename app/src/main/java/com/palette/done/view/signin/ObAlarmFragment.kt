package com.palette.done.view.signin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.palette.done.DoneApplication
import com.palette.done.data.db.entity.Alarm
import com.palette.done.data.enums.DaysType
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.databinding.FragmentObAlarmBinding
import com.palette.done.receiver.DoneBroadcastReceiver
import com.palette.done.receiver.DoneBroadcastReceiver.Companion.NOTIFICATION_ID
import com.palette.done.view.adapter.ObAlarmRecyclerViewAdapter
import com.palette.done.view.decoration.RecyclerViewDecoration
import com.palette.done.view.util.AlarmManagerUtil
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.OnBoardingViewModel
import com.palette.done.viewmodel.OnBoardingViewModelFactory
import java.text.DecimalFormat


class ObAlarmFragment : Fragment() {

    private var _binding: FragmentObAlarmBinding? = null
    private val binding get() = _binding!!

    private val onBoardingVM: OnBoardingViewModel by activityViewModels {
        OnBoardingViewModelFactory(
            MemberRepository(),
            (requireActivity().application as DoneApplication).doneRepository
        )
    }

    private val INTERVAL = 5
    private val FORMATTER: DecimalFormat = DecimalFormat("00")

    private var minutePicker: NumberPicker? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentObAlarmBinding.inflate(inflater, container, false)

        binding.btnNext.setOnClickListener {
            val hour = binding.tpAlarmTime.hour
            val minute = binding.tpAlarmTime.minute * INTERVAL

            val alarm = Alarm(
                hour = hour,
                min = minute,
                days = onBoardingVM.alarmWeekday.value?.map { it.idx }?.toSet() ?: setOf()
            )

            AlarmManagerUtil(requireContext().applicationContext).cancelAndSetAlarm(alarm)
            onBoardingVM.insertOrUpdateAlarm(alarm)

            Toast.makeText(requireContext(), "$hour $minute", Toast.LENGTH_LONG).show()
        }

        onBoardingVM.alarmWeekday.observe(viewLifecycleOwner) {
            binding.btnNext.isEnabled = onBoardingVM.alarmWeekday.value!!.isNotEmpty()
        }

        setMinutePicker()

        setNextButton()
        setTimeTicker()
        setAlarmWeekBtn()

        return binding.root
    }

    private fun setNextButton() {
        // 기존 스택 없애고 메인 화면이 루트
//        binding.btnNext.setOnClickListener {
//            onBoardingVM.patchMemberProfile()
//            onBoardingVM.patchSuccess.observe(viewLifecycleOwner) {
//                if (it) {
//                    val intent = Intent(requireContext(), MainActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(intent)
//                }
//            }
//        }
    }

    fun setMinutePicker() {
        val numValues = 60 / INTERVAL
        val displayedValues = arrayOfNulls<String>(numValues)
        for (i in 0 until numValues) {
            displayedValues[i] = FORMATTER.format(i * INTERVAL)
        }
        val minute: View = binding.tpAlarmTime.findViewById(
            Resources.getSystem().getIdentifier("minute", "id", "android")
        )
        if (minute is NumberPicker) {
            minutePicker = minute
            minutePicker!!.minValue = 0
            minutePicker!!.maxValue = numValues - 1
            minutePicker!!.displayedValues = displayedValues
        }
    }

    private fun setTimeTicker() {
        binding.tpAlarmTime.hour = 9
        binding.tpAlarmTime.minute = 0
        binding.tpAlarmTime.setOnTimeChangedListener { view, hourOfDay, minute ->
            onBoardingVM.alarmHour.value = hourOfDay
            onBoardingVM.alarmMin.value = minute * 5
            Log.d(
                "ob_time_changed",
                "${onBoardingVM.alarmHour.value} : ${onBoardingVM.alarmMin.value}"
            )
        }
    }

    private fun setAlarmWeekBtn() {
        binding.rcWeekBtn.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rcWeekBtn.addItemDecoration(RecyclerViewDecoration(9))
        val util = Util()
        val display = requireActivity().windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        val adapter = ObAlarmRecyclerViewAdapter(size.x - util.dpToPx(40 + 9 * 6))
        binding.rcWeekBtn.adapter = adapter
        adapter.setWeekItemClickListener(object :
            ObAlarmRecyclerViewAdapter.OnWeekItemClickListener {
            override fun onClick(v: View, daysType: DaysType) {
                v.isSelected = !v.isSelected
                onBoardingVM.setButtonSelectedAction(v.isSelected, daysType)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}