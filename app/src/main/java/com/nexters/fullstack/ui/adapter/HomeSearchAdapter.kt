package com.nexters.fullstack.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import com.nexters.fullstack.base.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nexters.fullstack.databinding.ItemLabelBinding
import com.nexters.fullstack.source.Label
import com.nexters.fullstack.source.LabelSource
import com.nexters.fullstack.ui.holder.MyLabelViewHolder

class HomeSearchAdapter : BaseAdapter<Label>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = MyLabelViewHolder(ItemLabelBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MyLabelViewHolder -> {
                holder.bind(items[position])
                holder.itemView.setOnClickListener {
                    getItemClickListener()?.invoke(it, holder.adapterPosition, items[holder.adapterPosition])
                }
            }
        }
    }
}