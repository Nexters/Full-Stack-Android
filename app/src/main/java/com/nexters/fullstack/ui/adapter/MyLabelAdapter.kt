package com.nexters.fullstack.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nexters.fullstack.NotFoundViewType
import com.nexters.fullstack.base.BaseAdapter
import com.nexters.fullstack.databinding.ItemLabelBinding
import com.nexters.fullstack.databinding.ItemListLabelBinding
import com.nexters.fullstack.databinding.ItemSelectedLabelBinding
import com.nexters.fullstack.source.LabelSource
import com.nexters.fullstack.ui.holder.LabelListViewHolder
import com.nexters.fullstack.ui.holder.LabelingSelectedViewHolder
import com.nexters.fullstack.ui.holder.MyLabelViewHolder
import com.nexters.fullstack.ui.holder.RecommendLabelViewHolder

class MyLabelAdapter : BaseAdapter<LabelSource>() {
    private val _selectedLabel = mutableListOf<LabelSource>()
    val selectedLabel: List<LabelSource>
        get() = _selectedLabel

    private val onLabelClickListener = { position: Int ->
        val getLabelSource = getItem(position)
        if (_selectedLabel.contains(getLabelSource)) {
            _selectedLabel.remove(getLabelSource)
        } else {
            _selectedLabel.add(getLabelSource)
        }
        Log.e("selectedLabel", _selectedLabel.toString())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            LabelSource.RECOMMEND -> RecommendLabelViewHolder(
                ItemLabelBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
            LabelSource.LIST -> {
                LabelListViewHolder(
                    onLabelClickListener,
                    ItemListLabelBinding.inflate(LayoutInflater.from(parent.context))
                )
            }
            LabelSource.SELECTED -> {
                LabelingSelectedViewHolder(
                    ItemSelectedLabelBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        )
                    )
                )
            }
            else -> throw NotFoundViewType
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecommendLabelViewHolder -> holder.bind(items[position])
            is LabelListViewHolder -> {
                holder.bind(_selectedLabel, items[position])
            }
        }
    }

    override fun getItemViewType(position: Int) = items[position].type

    fun selectedLabelClear() {
        _selectedLabel.clear()
    }
}