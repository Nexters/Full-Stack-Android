package com.nexters.fullstack.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nexters.fullstack.base.BaseAdapter
import com.nexters.fullstack.databinding.RecyclerAlbumItemBinding
import com.nexters.fullstack.source.local.DomainUserImage
import com.nexters.fullstack.ui.holder.LocalImageViewHolder

class LocalImageAdapter : BaseAdapter<DomainUserImage>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LocalImageViewHolder(
            RecyclerAlbumItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LocalImageViewHolder -> {}
        }
    }
}