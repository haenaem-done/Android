package com.palette.done.view.signin

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.palette.done.DoneApplication
import com.palette.done.R
import com.palette.done.data.remote.repository.MemberRepository
import com.palette.done.databinding.ActivityOnBoardingBinding
import com.palette.done.view.adapter.ViewPagerAdapter
import com.palette.done.viewmodel.OnBoardingViewModel
import com.palette.done.viewmodel.OnBoardingViewModelFactory

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    private val onBoardingVM: OnBoardingViewModel by viewModels {
        OnBoardingViewModelFactory(
            MemberRepository(),
            (application as DoneApplication).doneRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DoneApplication.pref.level = 1

        setBackButtonAndIndicator()
        setViewPager()
    }

    private fun setBackButtonAndIndicator() {
        binding.btnBack.setOnClickListener {
            setBackButton()
        }
    }

    private fun setBackButton() {
        val current = binding.viewPagerOnBoarding.currentItem
        when(current) {
            0 -> {
                finish()
            }
            1 ->{
                binding.viewPagerOnBoarding.currentItem = 0
                binding.ivIndicatorSecond.setImageResource(R.drawable.ic_indicator_left)
                binding.ivIndicatorThird.setImageResource(R.drawable.ic_indicator_left)}
//            2 -> {
//                binding.viewPagerOnBoarding.currentItem = 1
//                binding.ivIndicatorSecond.setImageResource(R.drawable.ic_indicator_now)
//                binding.ivIndicatorThird.setImageResource(R.drawable.ic_indicator_left)
//            }
        }
    }

    private fun setViewPager() {
        val fragments = arrayListOf<Fragment>(ObAlarmFragment(), ObNicknameFragment(), ObTypeFragment())
//        val fragments = arrayListOf<Fragment>(ObNicknameFragment(), ObTypeFragment())
        val adapter = ViewPagerAdapter(this, fragments)
        binding.viewPagerOnBoarding.adapter = adapter
        binding.viewPagerOnBoarding.isUserInputEnabled = false  // swipe action 제거
    }


    override fun onBackPressed() {
//        super.onBackPressed()
        setBackButton()
    }


}