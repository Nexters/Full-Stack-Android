package com.nexters.fullstack.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nexters.fullstack.BaseViewModel
import com.nexters.fullstack.Input
import com.nexters.fullstack.Output
import com.nexters.fullstack.source.Label

class LabelOutAppViewModel : BaseViewModel(){

    private val _imageUri : MutableLiveData<Uri> = MutableLiveData()
    val imageUri : LiveData<Uri> get() = _imageUri

    private val _myLabels : MutableLiveData<ArrayList<Label>> = MutableLiveData()
    val myLabels : LiveData<ArrayList<Label>> get() = _myLabels


    val input = object : LabelOutAppInput{
        override fun setImageUri(uri: Uri) {
            _imageUri.value = uri
        }
    }

    val output = object : LabelOutAppOutput {
    }

    interface LabelOutAppInput : Input{
        fun setImageUri(uri: Uri)
    }

    interface LabelOutAppOutput : Output{
    }

    init {
        val array = ArrayList<Label>()
        array.add(Label(Label.RECOMMEND, "test1"))
        array.add(Label(Label.DEFAULT,"test21111111"))
        array.add(Label(Label.DEFAULT,"test3"))
        array.add(Label(Label.DEFAULT,"te3"))
        array.add(Label(Label.DEFAULT,"test3"))
        array.add(Label(Label.DEFAULT,"test11111113"))
        array.add(Label(Label.DEFAULT,"test3"))
        _myLabels.value = array
    }
}