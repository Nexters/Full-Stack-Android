package com.nexters.fullstack.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log

import android.view.inputmethod.EditorInfo
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.nexters.fullstack.BR
import com.nexters.fullstack.Constants
import com.nexters.fullstack.NotFoundViewState
import com.nexters.fullstack.R
import com.nexters.fullstack.base.BaseActivity
import com.nexters.fullstack.databinding.ActivityLabelOutappBinding
import com.nexters.fullstack.ext.hideKeyboard
import com.nexters.fullstack.source.LabelSource
import com.nexters.fullstack.ui.adapter.MyLabelAdapter
import com.nexters.fullstack.ui.adapter.OutAppLabelAdapter
import com.nexters.fullstack.ui.adapter.SelectedLabelAdapter
import com.nexters.fullstack.ui.decoration.SpaceBetweenRecyclerDecoration
import com.nexters.fullstack.viewmodel.LabelOutAppViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LabelOutAppActivity : BaseActivity<ActivityLabelOutappBinding, LabelOutAppViewModel>() {
    override val layoutRes: Int = R.layout.activity_label_outapp
    override val viewModel: LabelOutAppViewModel by viewModel()

    private val myLabelAdapter : OutAppLabelAdapter = OutAppLabelAdapter(LabelOutAppViewModel.ViewState.MY_LABEL)
    private val selectedLabelAdapter : SelectedLabelAdapter = SelectedLabelAdapter()
    private val recentlyLabelAdapter : OutAppLabelAdapter = OutAppLabelAdapter(LabelOutAppViewModel.ViewState.RECENT_LABEL)
    private val searchResultAdapter : OutAppLabelAdapter = OutAppLabelAdapter(LabelOutAppViewModel.ViewState.SEARCH_RESULT)
    private val addLabelAdapter : OutAppLabelAdapter = OutAppLabelAdapter(LabelOutAppViewModel.ViewState.NO_RESULT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind {
            setVariable(BR.vm, viewModel)
        }
        initData()
        initView()
        initListener()
        initObserver()
    }

    private fun initData(){
        when{
            intent?.action == Intent.ACTION_SEND
                    && intent.type?.startsWith(Constants.IMAGE_PREFIX) == true -> {
                (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let { uri: Uri ->
                    viewModel.loadImage(uri)
                }
            }
        }
    }

    private fun initView() {
        with(viewModel.state()) {
            imageUri.observe(this@LabelOutAppActivity, {
                Glide.with(this@LabelOutAppActivity)
                    .load(it)
                    .into(binding.ivScreenshot)
            })
        }

        with(binding){

            val spaceDecoration = SpaceBetweenRecyclerDecoration(RV_SPACING_DP, RV_SPACING_DP)
            rvLabel.run {
                myLabelAdapter.addItems(viewModel.state().myLabels.value ?: ArrayList())
                adapter = myLabelAdapter
                rvLabel.addItemDecoration(spaceDecoration)
                layoutManager = FlexboxLayoutManager(this@LabelOutAppActivity).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                }
                setHasFixedSize(true)
            }

            selectedLabelAdapter.addItems(viewModel.state().selectedLabels.value ?: ArrayList())
            rvSelectedLabel.adapter = selectedLabelAdapter
            rvSelectedLabel.addItemDecoration(spaceDecoration)

            recentlyLabelAdapter.addItems(viewModel.state().recentlySearch.value ?: ArrayList())
            searchResultAdapter.addItems(viewModel.state().searchResult.value ?: ArrayList())
            addLabelAdapter.addItems(ArrayList())
        }
    }

    private fun initListener(){
        with(binding){
            ivCancel.setOnClickListener {
                onBackPressed()
            }
            ivBack.setOnClickListener {
                viewModel.setViewState(LabelOutAppViewModel.ViewState.MY_LABEL)
            }
            ivClear.setOnClickListener {
                viewModel.clearSearchKeyword()
            }
            tvDone.setOnClickListener {
                viewModel.completeLabeling()
                // TODO finish activity and show toast
                finish()
            }
            etSearch.setOnFocusChangeListener { _, isFocused ->
                if(isFocused){
                    viewModel.setViewState(LabelOutAppViewModel.ViewState.RECENT_LABEL)
                    containerAppbar.setExpanded(false)
                }
                else{
                    etSearch.hideKeyboard()
                }
            }
            etSearch.setOnEditorActionListener { _, action, _ ->
                var handled = false
                if(action == EditorInfo.IME_ACTION_DONE){
                    etSearch.clearFocus()
                    handled = true
                }
                handled
            }
        }
        myLabelAdapter.setItemClickListener { _, i, _ ->
            viewModel.selectLabel(i)
        }
        recentlyLabelAdapter.setItemClickListener { view, i, labelSource ->
            Log.e("test", "recent")
        }
        searchResultAdapter.setItemClickListener { view, i, labelSource ->
            viewModel.selectLabel(labelSource.name)
            viewModel.setViewState(LabelOutAppViewModel.ViewState.MY_LABEL)
        }
        selectedLabelAdapter.setItemClickListener { _, i, _ ->
            viewModel.deselectLabel(i)
        }
        addLabelAdapter.setItemClickListener { view, i, labelSource ->
            // TODO start add label activity and update my labels
            startActivity(
                Intent(
                    this@LabelOutAppActivity,
                    CreateLabelActivity::class.java
                ))
        }
    }

    private fun initObserver(){
        with(viewModel.state()){
            myLabels.observe(this@LabelOutAppActivity, {
                myLabelAdapter.calDiff(it as MutableList<LabelSource>)
            })
            selectedLabels.observe(this@LabelOutAppActivity, {
                selectedLabelAdapter.calDiff(it as MutableList<LabelSource>)
                binding.rvSelectedLabel.scrollToPosition(0)
            })
            searchResult.observe(this@LabelOutAppActivity, {
                searchResultAdapter.calDiff(it as MutableList<LabelSource>)
            })
            searchKeyword.observe(this@LabelOutAppActivity, {
                if(it == "" || it == null){
                    if(viewModel.state().viewState.value != LabelOutAppViewModel.ViewState.MY_LABEL){
                        viewModel.setViewState(LabelOutAppViewModel.ViewState.RECENT_LABEL)
                    }
                }
                else{
                    if(viewModel.search()){
                        viewModel.setViewState(LabelOutAppViewModel.ViewState.SEARCH_RESULT)
                    }
                    else {
                        viewModel.setViewState(LabelOutAppViewModel.ViewState.NO_RESULT)
                        addLabelAdapter.clearItems()
                        addLabelAdapter.addItem(LabelSource(type = 2002, color = "", name = it))
                    }
                }
            })
            viewState.observe(this@LabelOutAppActivity, {
                when(it){
                    LabelOutAppViewModel.ViewState.MY_LABEL -> {
                        binding.rvLabel.adapter = myLabelAdapter
                        binding.containerAppbar.setExpanded(true)
                        binding.etSearch.clearFocus()
                        viewModel.clearSearchKeyword()
                    }
                    LabelOutAppViewModel.ViewState.RECENT_LABEL -> {
                        binding.rvLabel.adapter = recentlyLabelAdapter
                    }
                    LabelOutAppViewModel.ViewState.SEARCH_RESULT -> {
                        binding.rvLabel.adapter = searchResultAdapter
                    }
                    LabelOutAppViewModel.ViewState.NO_RESULT -> {
                        binding.rvLabel.adapter = addLabelAdapter
                    }
                    else -> throw NotFoundViewState
                }
            })
        }
    }



    companion object{
        const val RV_SPACING_DP = 5
    }
}
