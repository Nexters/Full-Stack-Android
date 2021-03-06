package com.nexters.fullstack.ui.holder

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nexters.fullstack.databinding.ItemStackViewBinding
import com.nexters.fullstack.source.LocalFile

class MainStackItemHolder(private val binding: ItemStackViewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: LocalFile) {
        Glide.with(binding.screenShot)
            .load(item.url)
            .into(binding.screenShot)
    }
}