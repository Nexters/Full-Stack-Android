package com.nexters.fullstack.ui.activity

import android.os.Bundle
import com.nexters.fullstack.BR
import com.nexters.fullstack.R
import com.nexters.fullstack.base.BaseActivity
import com.nexters.fullstack.databinding.ActivityCreateLabelBinding
import com.nexters.fullstack.source.ViewState
import com.nexters.fullstack.viewmodel.LabelingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateLabelActivity : BaseActivity<ActivityCreateLabelBinding, LabelingViewModel>() {
    override val layoutRes: Int = R.layout.activity_create_label

    override val viewModel: LabelingViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        bind {
            setVariable(BR.vm, viewModel)
        }

        binding.palletLayout.setOnLabelClickListener = {
            viewModel.input.clickLabel(it)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}