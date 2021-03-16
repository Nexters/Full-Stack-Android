package com.nexters.fullstack.ui.fragment

import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieDrawable
import com.nexters.fullstack.R
import com.nexters.fullstack.base.BaseFragment
import com.nexters.fullstack.databinding.FragmentOnboardingBinding
import com.nexters.fullstack.source.ActivityResultData
import com.nexters.fullstack.viewmodel.OnBoardingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnBoardingFragment : BaseFragment<FragmentOnboardingBinding, OnBoardingViewModel>() {
    override val layoutRes: Int = R.layout.fragment_onboarding
    override val viewModel: OnBoardingViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    private fun bindView() {
        val data : OnBoardingViewModel.OnBoardingData = viewModel.getItem(requireArguments().getInt(BUNDLE_KEY))

        binding.tvMain.text = data.main
        binding.tvSub.text = data.sub
        binding.lottie.setAnimation(LOTTIE_PREFIX + data.order + LOTTIE_SUFFIX)
        binding.lottie.playAnimation()
        binding.lottie.repeatCount = LottieDrawable.INFINITE

        when(data.order){
            1 -> binding.indicator1.isSelected = true
            2 -> binding.indicator2.isSelected = true
            3 -> binding.indicator3.isSelected = true
        }
    }

    companion object {
        private const val LOTTIE_PREFIX = "onboarding"
        private const val LOTTIE_SUFFIX = ".json"
        private const val BUNDLE_KEY = "order"
        fun newInstance(order: Int): OnBoardingFragment {
            val fragment = OnBoardingFragment()
            val bundle = Bundle()
            bundle.putInt(BUNDLE_KEY, order)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onActivityResult(activityResultData: ActivityResultData) {
        // no
    }
}