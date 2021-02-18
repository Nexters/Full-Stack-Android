package com.nexters.fullstack.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nexters.fullstack.Constants
import com.nexters.fullstack.base.BaseActivity
import com.nexters.fullstack.R
import com.nexters.fullstack.databinding.ActivityLabelingBinding
import com.nexters.fullstack.viewmodel.LabelingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.nexters.fullstack.ext.loadFragment
import com.nexters.fullstack.ext.removeFragment
import com.nexters.fullstack.source.LocalFile
import com.nexters.fullstack.source.ViewState
import com.nexters.fullstack.ui.fragment.LabelCreateFragment
import com.nexters.fullstack.widget.RequestExitDialog
import com.nexters.fullstack.ui.fragment.LabelSelectFragment
import com.nexters.fullstack.ui.fragment.label.LabelSearchFragment

class LabelingActivity : BaseActivity<ActivityLabelingBinding, LabelingViewModel>() {
    override val layoutRes: Int = R.layout.activity_labeling
    override val viewModel: LabelingViewModel by viewModel()

    private val dialog: RequestExitDialog by lazy {
        RequestExitDialog()
    }

    private lateinit var  labelSelectFragment: LabelSelectFragment
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        labelSelectFragment = LabelSelectFragment.getInstance(intent.getParcelableExtra(Constants.LABEL_BUNDLE_KEY))
        activeFragment = labelSelectFragment
        setOnClickListener()
        initToolbar()
        initView()
        observe()
        bind { }
        Log.e("hi", intent.getParcelableExtra<LocalFile>(Constants.LABEL_BUNDLE_KEY).toString())
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24)
    }

    private fun setOnClickListener() {
        with(viewModel.input) {
            binding.addLabel.setOnClickListener { clickAppbar(ViewState.Add) }
            binding.searchLabel.setOnClickListener { clickAppbar(ViewState.Search) }
        }
    }

    private fun initView() {
        /**
         **   todo viewmodel 에서 클릭 시 state변경
         *   ex. State ( Init, Search, Add )
         *   observe -> State
         *   when(state) {
         *      is Init -> changeFragment()   todo binding.title.text = "라벨 추가"
         *      is Search -> changeFragment()
         *      ...
         *   }
         **/
        supportFragmentManager.loadFragment(
            binding.frame.id,
            labelSelectFragment
        )

        supportFragmentManager.beginTransaction().show(labelSelectFragment).commit()
    }

    private fun observe() {
        with(viewModel.output) {
            viewState().observe(this@LabelingActivity) { viewState ->
                when (viewState) {
                    is ViewState.Selected -> changeFragment(activeFragment, labelSelectFragment)
                    is ViewState.Add -> startActivity(
                        Intent(
                            this@LabelingActivity,
                            CreateLabelActivity::class.java
                        )
                    )
                    is ViewState.Search -> startActivity(
                        Intent(
                            this@LabelingActivity,
                            SearchLabelActivity::class.java
                        )
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        supportFragmentManager.removeFragment(
            labelSelectFragment
        )
        super.onDestroy()
    }

    private fun changeFragment(oldFragment: Fragment, newFragment: Fragment) {
        supportFragmentManager.beginTransaction().hide(oldFragment).show(newFragment).commit()
        activeFragment = newFragment
    }

    override fun onSupportNavigateUp(): Boolean {
        dialog.show(supportFragmentManager, "")
        return true
    }
}