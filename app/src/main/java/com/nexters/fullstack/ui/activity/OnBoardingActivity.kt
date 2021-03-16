package com.nexters.fullstack.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nexters.fullstack.MainActivity
import com.nexters.fullstack.R
import com.nexters.fullstack.base.BaseActivity
import com.nexters.fullstack.databinding.ActivityOnboardingBinding
import com.nexters.fullstack.ui.fragment.OnBoardingFragment
import com.nexters.fullstack.util.PrefDataStoreManager
import com.nexters.fullstack.viewmodel.OnBoardingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardingActivity : BaseActivity<ActivityOnboardingBinding, OnBoardingViewModel>() {
    override val layoutRes: Int = R.layout.activity_onboarding
    override val viewModel: OnBoardingViewModel by viewModel()

    private lateinit var viewPagerAdapter : PagerAdapter
    private val prefDataStoreManager = PrefDataStoreManager()
//    <  using shared preference  >
//    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        <  using shared preference  >
//        sharedPref = this.getPreferences(MODE_PRIVATE)
        initView()
        initListner()
    }

    private fun initView(){
        viewPagerAdapter = PagerAdapter(this)
        binding.pager.adapter = viewPagerAdapter
    }

    private fun initListner(){
        with(binding){
            tvButton.setOnClickListener {
                with(pager){
                    if(currentItem == viewPagerAdapter.itemCount-1){
                        Log.e("test","last")
                        startMainActivity()
                    }else{
                        currentItem += 1
                    }
                }
            }
            tvSkip.setOnClickListener {
                startMainActivity()
            }
        }
    }

    private fun startMainActivity(){
//        <  using shared preference  >
//        sharedPref.edit().putBoolean(Constants.FIRST_LOADING, false).apply()
        CoroutineScope(Dispatchers.IO).launch {
            prefDataStoreManager.updateIsFirst(false)
            startActivity(Intent(this@OnBoardingActivity, MainActivity::class.java))
        }
    }

    private inner class PagerAdapter(fa : FragmentActivity) : FragmentStateAdapter(fa){
        override fun getItemCount(): Int = PAGE_NUM
        override fun createFragment(position: Int): Fragment = OnBoardingFragment.newInstance(position)
    }

    companion object{
        private const val PAGE_NUM = 3
    }
}