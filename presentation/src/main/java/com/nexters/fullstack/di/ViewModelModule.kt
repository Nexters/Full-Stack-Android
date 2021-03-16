package com.nexters.fullstack.di

import com.nexters.fullstack.constants.KoinNamed
import com.nexters.fullstack.mapper.LocalToPresentMapper
import com.nexters.fullstack.mapper.Mapper
import com.nexters.fullstack.source.PresentLocalFile
import com.nexters.fullstack.source.data.LocalImageDomain
import com.nexters.fullstack.viewmodel.BottomSheetViewModel
import com.nexters.fullstack.viewmodel.LabelOutAppViewModel
import com.nexters.fullstack.viewmodel.LabelingViewModel
import com.nexters.fullstack.viewmodel.MainViewModel
import com.nexters.fullstack.viewmodel.OnBoardingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val presentationMapper = module {
    single<Mapper<LocalImageDomain, PresentLocalFile>> { LocalToPresentMapper() }
}

val viewModelModule = module {
    viewModel { LabelOutAppViewModel(get()) }
    viewModel { MainViewModel(get(), get(named("label")), get(named("image")), get()) }
    viewModel { OnBoardingViewModel() }
    viewModel { LabelingViewModel(get(), get(), get(named(KoinNamed.IMAGE)), get()) }
    viewModel { BottomSheetViewModel() }
}

