package com.nexters.fullstack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nexters.fullstack.BaseViewModel
import com.nexters.fullstack.Input
import com.nexters.fullstack.Output
import com.nexters.fullstack.source.LabellingState
import com.nexters.fullstack.source.MainLabel
import com.nexters.fullstack.source.PresentLocalFile
import com.nexters.fullstack.source.State
import com.nexters.fullstack.mapper.Mapper
import com.nexters.fullstack.source.*
import com.nexters.fullstack.source.data.LocalImageDomain
import com.nexters.fullstack.source.local.DomainUserImage
import com.nexters.fullstack.usecase.base.BaseUseCase
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class MainViewModel(
    private val flipUseCase: BaseUseCase<LabellingState, Boolean>,
    albumLoadUseCase: BaseUseCase<String, Single<List<LocalImageDomain>>>,
    localImageDBUseCase: BaseUseCase<Unit, Maybe<List<DomainUserImage>>>,
    mapper: Mapper<LocalImageDomain, PresentLocalFile>
) : BaseViewModel() {

    private val disposable = CompositeDisposable()
    private val flipStateSubject = PublishSubject.create<LabellingState>()
    private val onClickButton = PublishSubject.create<State>()

    private val mainLabel = MutableLiveData<MainLabel>()
    private val startLabeling = MutableLiveData<Unit>()
    private val _localImage = MutableLiveData<List<DomainUserImage>>()
    private val _imageItemCount = MutableLiveData<Int>()

    val input = object : MainInput {
        override fun onClickedButton(state: State) = onClickButton.onNext(state)

        override fun emitLabellingState(state: LabellingState) = flipStateSubject.onNext(state)
    }

    val output = object : MainOutput {
        override fun state(): LiveData<MainLabel> {
            return mainLabel
        }

        override fun getLocalImageCount(): LiveData<Int> = _imageItemCount

        override fun startLabelling() = startLabeling
    }

    init {
        val images: Single<List<PresentLocalFile>> =
            albumLoadUseCase.buildUseCase(SCREEN_SHOT_PRIFIX)
                .map {
                    it.map { localLabel ->
                        mapper.toData(localLabel)
                    }
                }.cache()

        val state = onClickButton.cache()

        val localImage: Maybe<List<DomainUserImage>> =
            localImageDBUseCase.buildUseCase(Unit)
                .cache()

        disposable.addAll(
            images
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    mainLabel.value = MainLabel(images = response)
                    _imageItemCount.value = response.size
                }, {}),

            localImage
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _localImage.value = it
                }, {}),

            Observable.combineLatest(
                images.toObservable(),
                state,
                ::MainLabel
            ).subscribe({ response ->
                if (response.state == State.Approve) {
                    startLabeling.value = Unit
                }
                mainLabel.value = response
            }, {
                it.printStackTrace()
            })
        )
    }

    interface MainInput : Input {
        fun onClickedButton(state: State)
        fun emitLabellingState(state: LabellingState)
    }

    interface MainOutput : Output {
        fun state(): LiveData<MainLabel>

        fun getLocalImageCount(): LiveData<Int>

        //Router
        fun startLabelling(): LiveData<Unit>
    }

    companion object {
        private const val SCREEN_SHOT_PRIFIX = "Screenshots"
    }
}