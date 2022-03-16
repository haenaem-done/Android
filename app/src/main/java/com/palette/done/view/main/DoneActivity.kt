package com.palette.done.view.main

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.db.entity.Done
import com.palette.done.data.remote.repository.DoneServerRepository
import com.palette.done.databinding.ActivityDoneBinding
import com.palette.done.view.adapter.DoneAdapter
import com.palette.done.view.main.done.DoneFragment
import com.palette.done.view.main.today.TodayRecordActivity
import com.palette.done.view.util.Util
import com.palette.done.viewmodel.*
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import com.skydoves.powermenu.kotlin.showAsDropDown
import java.util.*

class DoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoneBinding
    private val dateVM: DoneDateViewModel by viewModels() {
        DoneDateViewModelFactory((application as DoneApplication).doneRepository)
    }
    private val doneVM: DoneEditViewModel by viewModels() {
        DoneEditViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val planVM: PlanViewModel by viewModels() {
        PlanViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val routineVM: RoutineViewModel by viewModels() {
        RoutineViewModelFactory(DoneServerRepository(), DoneApplication().doneRepository)
    }
    private val categoryVM: CategoryViewModel by viewModels() {
        CategoryViewModelFactory(DoneApplication().doneRepository)
    }

    private var doneAdapter = DoneAdapter()
    private lateinit var popup: PowerMenu

    private val util = Util()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clickedDate = intent.getStringExtra("clickedDate")
        Log.d("date_clicked", "${clickedDate}")

        dateVM.setTitleDate(clickedDate!!)
        dateVM.transStringToCalendar(clickedDate)

        routineVM.initRoutine()
        planVM.initPlan()

        showDialog()

        setTitleDate()
        setButtonsDestination()

        initDoneListRecyclerView()

        popEditFrame()
        hideKeyboard()
    }

    private fun showDialog() {
        val empty = intent.getBooleanExtra("is_empty", false)
        if (empty) {
            val dialog = DoneDialog()
            dialog.show(supportFragmentManager, "DoneDialog")
        }
    }

    private fun initDoneListRecyclerView() {
        with(binding.rcDoneList) {
            adapter = doneAdapter
            layoutManager = LinearLayoutManager(context)
        }
        dateVM.doneList.observe(this) { doneLists ->
            Log.d("date_list_size", "${doneLists.size}")
            doneLists.let { doneAdapter.submitList(it) }
            if (doneLists.isEmpty()) {
                binding.tvDoneListCount.visibility = View.INVISIBLE
                binding.tvDoneList.text = getString(R.string.done_list_hint)
            } else {
                binding.tvDoneListCount.visibility = View.VISIBLE
                binding.tvDoneListCount.text = "${doneLists.size}"
                binding.tvDoneList.text = ""
            }
            // recyclerview 높이 지정
            lateinit var layoutParams: ConstraintLayout.LayoutParams
            if (doneLists.size > 6) {
                layoutParams = ConstraintLayout.LayoutParams(binding.rcDoneList.width, ConstraintLayout.LayoutParams.MATCH_PARENT)
                layoutParams.topToBottom = binding.tvDoneTitle.id
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

            } else {
                layoutParams = ConstraintLayout.LayoutParams(binding.rcDoneList.width, util.dpToPx(280))
                layoutParams.topToBottom = binding.tvDoneTitle.id
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
            binding.rcDoneList.layoutParams = layoutParams

        }

        doneAdapter.setDoneClickListener(object : DoneAdapter.OnDoneClickListener{
            // 던리스트 수정/삭제 메뉴
            override fun onDoneMenuClick(v: View, done: Done, position: Int) {
                popup = PowerMenu.Builder(this@DoneActivity)
                    .addItem(PowerMenuItem("수정"))
                    .addItem(PowerMenuItem("삭제"))
                    .setMenuRadius(util.dpToPx(8).toFloat())
                    .setTextColor(ContextCompat.getColor(this@DoneActivity, R.color.black))
                    .setTextSize(14)
                    .setTextGravity(Gravity.CENTER)
                    .setMenuColor(ContextCompat.getColor(this@DoneActivity, R.color.white))
                    .setDivider(ContextCompat.getDrawable(this@DoneActivity, R.drawable.popup_divider))
                    .setDividerHeight(util.dpToPx(1))
                    .setPadding(9)
                    .setWidth(util.dpToPx(108))
                    .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                    .setSelectedMenuColor(ContextCompat.getColor(this@DoneActivity, R.color.doneDetailColor))
                    .setOnMenuItemClickListener { menu, item ->
                        when(menu) {
                            0 -> {
                                popup.dismiss()
                                doneVM.oldDone = done
                                doneVM.oldDoneIndex = position
                                categoryVM._selectedCategory.value = done.categoryNo
                                supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment(DoneMode.EDIT_DONE)).commit()
                                binding.flDoneWrite.visibility = View.VISIBLE
                            }
                            1 -> {
                                doneVM.deleteDoneList(done.doneId)
                                popup.dismiss()
                            }
                        }
                    }
                    .build()
                popup.showAsDropDown(v, v.measuredWidth/2-popup.contentViewWidth+util.dpToPx(14), -v.measuredHeight/2-util.dpToPx(6))
            }

            override fun onDoneRootClick(v: View) {
                supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment(DoneMode.DONE)).commit()
                binding.flDoneWrite.visibility = View.VISIBLE
            }
        })
    }

    private fun setButtonsDestination() {
        with(binding) {
            btnCalendarBack.setOnClickListener {
                finish()
            }
            btnPlan.setOnClickListener {
                val intent = Intent(this@DoneActivity, PlanRoutineActivity::class.java)
                val date = dateVM.getTitleDate()
                intent.putExtra("mode", ItemMode.PLAN.name)
                intent.putExtra("date", date)  // 현재 던리스트의 날짜
                startActivity(intent)
            }
            llTodayRecord.setOnClickListener {
                val intent = Intent(this@DoneActivity, TodayRecordActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setTitleDate() {
        with(binding) {
            btnDayBefore.setOnClickListener {
                dateVM.clickedBackwardDay()
            }
            btnDayAfter.setOnClickListener {
                dateVM.clickedForwardDay()
            }
        }
        dateVM.titleDate.observe(this) {
            binding.tvDate.text = dateVM.titleDate.value
        }
    }

    private fun popEditFrame() {
        binding.tvDoneList.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment(DoneMode.DONE)).commit()
            binding.flDoneWrite.visibility = View.VISIBLE
        }
        binding.rcDoneList.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(binding.flDoneWrite.id, DoneFragment(DoneMode.DONE)).commit()
            binding.flDoneWrite.visibility = View.VISIBLE
        }
        binding.rootView.setOnClickListener {
            hideKeyboard()  // visibility보다 먼저 처리해야 함
            binding.flDoneWrite.visibility = View.GONE
        }
        binding.subRootView.setOnClickListener {
            hideKeyboard()  // visibility보다 먼저 처리해야 함
            binding.flDoneWrite.visibility = View.GONE
        }
    }

    fun closeEditFrame() {
        binding.flDoneWrite.visibility = View.GONE
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }


}