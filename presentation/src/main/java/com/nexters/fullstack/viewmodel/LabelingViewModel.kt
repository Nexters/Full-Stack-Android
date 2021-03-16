package com.nexters.fullstack.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nexters.fullstack.BaseViewModel
import com.nexters.fullstack.Input
import com.nexters.fullstack.Output
import com.nexters.fullstack.mapper.LabelSourceMapper
import com.nexters.fullstack.mapper.LabelingMapper
import com.nexters.fullstack.mapper.PresenterLocalFileMapper
import com.nexters.fullstack.source.*
import com.nexters.fullstack.source.local.DomainUserImage
import com.nexters.fullstack.usecase.ImageLabelingUseCase
import com.nexters.fullstack.usecase.LabelingUseCase
import com.nexters.fullstack.usecase.LoadLabelUseCase
import com.nexters.feature.ui.data.pallet.PalletItem
import com.nexters.fullstack.source.*
import com.nexters.fullstack.usecase.LoadImageUseCase
import com.nexters.fullstack.usecase.base.BaseUseCase
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class LabelingViewModel(
    private val labelingUseCase: LabelingUseCase,
    loadLabelUseCase: LoadLabelUseCase,
    loadImageUseCase: BaseUseCase<Unit, Maybe<List<DomainUserImage>>>,
    private val imageLabelingUseCase: ImageLabelingUseCase
) : BaseViewModel() {
    private val _viewState = MutableLiveData<ViewState>()
    private val _finish = MutableLiveData<Unit>()
    private val _isEmptyLabel = MutableLiveData(true)
    private val _labels = MutableLiveData<LocalLabel>()

    private val _images = MutableLiveData<List<DomainUserImage>>()

    val _didWriteLabelInfo = MutableLiveData(false)
    private var makeMainLabelSource: MainMakeLabelSource? = null
    private val _clickedLabel = PublishSubject.create<PalletItem>()
    private val _labelText = PublishSubject.create<String>()
    val labelQuery = MutableLiveData("")

    private val disposable = CompositeDisposable()

    fun onTextChanged(s: CharSequence) = _labelText.onNext(s.toString())


    val output = object : LabelingOutput {
        override fun viewState(): LiveData<ViewState> = _viewState
        override fun finish(): LiveData<Unit> = _finish
        override fun isEmptyLocalLabel(): LiveData<Boolean> = _isEmptyLabel
        override fun labels(): LiveData<LocalLabel> = _labels
        override fun didWriteCreateLabelForm(): LiveData<Boolean> = _didWriteLabelInfo
        override fun getLabelQuery(): LiveData<String> = labelQuery
        override fun getImages(): LiveData<List<DomainUserImage>> = _images
    }

    val input = object : LabelingInput {
        override fun clickAppbar(viewState: ViewState) {
            _viewState.value = viewState
        }

        override fun clickLabel(palletItem: PalletItem) = _clickedLabel.onNext(palletItem)

        override fun clickSaveButton() {
            makeMainLabelSource?.let { source ->
                val mapper = LabelingMapper().fromData(source)

                disposable.addAll(
                    labelingUseCase.buildUseCase(mapper)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            _finish.value = Unit
                        }, {
                            it.printStackTrace()
                        }),

                    loadLabelUseCase.buildUseCase(Unit)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ localLabels ->
                            if (localLabels.isNotEmpty()) {
                                _isEmptyLabel.value = false
                                _labels.value = LocalLabel(localLabels)
                            } else {
                                _isEmptyLabel.value = true
                            }
                        }, { it.printStackTrace() }),
                )
            } ?: Log.e("labelSourceError", "makeMainLabelSource is Null")
        }

        override fun clickLabelAddButton() {
            _viewState.value = ViewState.Add
        }

        override fun clickCancelButton() {
            _finish.value = Unit
        }

        override fun clickLabelingButton(didClickList: List<LabelSource>, file: PresentLocalFile) {
            if (file.url.isEmpty()) {
                return
            }
            if (didClickList.isNullOrEmpty()) {
                return
            }
            val localFileMapper = PresenterLocalFileMapper.toData(file)
            val labelMapper = LabelSourceMapper.toData(didClickList)

            disposable.add(
                imageLabelingUseCase.buildUseCase(
                    DomainUserImage(labelMapper, localFileMapper)
                ).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _finish.value = Unit
                    }, { it.printStackTrace() })
            )
        }
    }

    init {
        val labels = loadLabelUseCase.buildUseCase(Unit).cache()

        val labelTextCache = _labelText.debounce(1000L, TimeUnit.MILLISECONDS).cache()

        val clickLabelCache = _clickedLabel.cache()

        disposable.addAll(
            labels
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ localLabels ->
                    if (localLabels.isNotEmpty()) {
                        _isEmptyLabel.value = false
                        _labels.value = LocalLabel(localLabels)
                    } else {
                        _isEmptyLabel.value = true
                    }
                }, { it.printStackTrace() }),

            loadImageUseCase.buildUseCase(Unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _images.value = it
                }, { it.printStackTrace() }),

            Observable.combineLatest(
                labelTextCache,
                clickLabelCache,
                ::MainMakeLabelSource
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ labelSource ->
                    val result = didWriteLabelInfo(labelSource)
                    makeMainLabelSource = labelSource
                    _didWriteLabelInfo.value = result
                }, { it.printStackTrace() }),
        )
        _viewState.value = ViewState.Selected
    }

    private fun didWriteLabelInfo(mainMakeLabelSource: MainMakeLabelSource): Boolean {
        var result = false
        return if (mainMakeLabelSource.labelText.isBlank()) {
            result
        } else {
            result = true
            result
        }
    }

    interface LabelingOutput : Output {
        fun viewState(): LiveData<ViewState>

        fun finish(): LiveData<Unit>

        fun isEmptyLocalLabel(): LiveData<Boolean>

        fun labels(): LiveData<LocalLabel>

        fun didWriteCreateLabelForm(): LiveData<Boolean>

        fun getLabelQuery(): LiveData<String>

        fun getImages(): LiveData<List<DomainUserImage>>
    }

    interface LabelingInput : Input {
        fun clickAppbar(viewState: ViewState)

        fun clickLabel(palletItem: PalletItem)

        fun clickSaveButton()

        fun clickLabelAddButton()

        fun clickCancelButton()

        fun clickLabelingButton(didClickList: List<LabelSource>, file: PresentLocalFile)
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}