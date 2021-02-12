package com.nexters.fullstack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nexters.fullstack.BaseViewModel
import com.nexters.fullstack.Input
import com.nexters.fullstack.Output
import com.nexters.fullstack.source.LocalLabel
import com.nexters.fullstack.source.ViewState
import com.nexters.fullstack.source.local.DomainUserLabel
import com.nexters.fullstack.usecase.LabelingUseCase
import com.nexters.fullstack.usecase.LoadLabelUseCase
import com.tsdev.feature.ui.data.PalletItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class LabelingViewModel(
    private val labelingUseCase: LabelingUseCase,
    loadLabelUseCase: LoadLabelUseCase
) : BaseViewModel() {
    private val _viewState = MutableLiveData<ViewState>()
    private val _finish = MutableLiveData<Unit>()
    private val _isEmptyLabel = MutableLiveData(true)
    private val _labels = MutableLiveData<LocalLabel>()
    val labelText = MutableLiveData<String>()
    private val _didWriteLabelInfo = MutableLiveData(false)
    private val _clickedLabel = PublishSubject.create<PalletItem>()

    private val disposable = CompositeDisposable()

    private val _colors = MutableLiveData(
        listOf(
            PalletItem("Yellow", "#353125", "#E8C15D", "#FFE299"),
            PalletItem("Orange", "#2E2218", "#EC9147", "#FFCBA1"),
            PalletItem("Red", "#2C1922", "#C76761", "#FFA799"),
            PalletItem("Pink", "#2D1D25", "#E089B5", "#FFC7E3"),
            PalletItem("Violet", "#2A1F38", "#A06EE5", "#D9C2FF"),
            PalletItem("Cobalt Blue", "#2B2B4D", "#6565E5", "#BFBFFF"),
            PalletItem("Blue", "#132334", "#4CA6FF", "#B2D9FF"),
            PalletItem("Peacock Green", "#182424", "#52CCCC", "#A1E5E5"),
            PalletItem("Green", "#1D2A24", "#3EA87A", "#B1E5CF"),
            PalletItem("Gray", "#282A2F", "#7B8399", "#CCDAFF")
        )
    )

    val output = object : LabelingOutput {
        override fun viewState(): LiveData<ViewState> = _viewState
        override fun finish(): LiveData<Unit> = _finish
        override fun isEmptyLocalLabel(): LiveData<Boolean> = _isEmptyLabel
        override fun labels(): LiveData<LocalLabel> = _labels
        override fun getBottomSheetLabels(): LiveData<List<PalletItem>> = _colors
        override fun didWriteCreateLabelForm(): LiveData<Boolean> = _didWriteLabelInfo
    }

    val input = object : LabelingInput {
        override fun clickAppbar(viewState: ViewState) {
            _viewState.value = viewState
        }

        override fun clickLabel(palletItem: PalletItem) = _clickedLabel.onNext(palletItem)

        override fun clickSaveButton(label: DomainUserLabel) {
            disposable.add(
                labelingUseCase.buildUseCase(label).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _finish.value = Unit
                    }, {
                        it.printStackTrace()
                    })
            )
        }

        override fun clickLabelAddButton() {
            _viewState.value = ViewState.Add
        }
    }

    init {
        val labels = loadLabelUseCase.buildUseCase(Unit).cache()

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
            _clickedLabel
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _didWriteLabelInfo.value = !labelText.value.isNullOrEmpty()
                }, { it.printStackTrace() })
        )
        _viewState.value = ViewState.Selected
    }

    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }

    interface LabelingOutput : Output {
        fun viewState(): LiveData<ViewState>

        fun finish(): LiveData<Unit>

        fun isEmptyLocalLabel(): LiveData<Boolean>

        fun labels(): LiveData<LocalLabel>

        fun getBottomSheetLabels(): LiveData<List<PalletItem>>

        fun didWriteCreateLabelForm(): LiveData<Boolean>
    }

    interface LabelingInput : Input {
        fun clickAppbar(viewState: ViewState)

        fun clickLabel(palletItem: PalletItem)

        fun clickSaveButton(label: DomainUserLabel)

        fun clickLabelAddButton()
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}