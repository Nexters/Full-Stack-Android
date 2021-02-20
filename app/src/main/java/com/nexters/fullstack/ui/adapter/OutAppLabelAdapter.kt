package com.nexters.fullstack.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nexters.fullstack.NotFoundViewType
import com.nexters.fullstack.base.BaseAdapter
import com.nexters.fullstack.databinding.ItemLabelBinding
import com.nexters.fullstack.databinding.ItemSearchAddBinding
import com.nexters.fullstack.databinding.ItemTitleCountBinding
import com.nexters.fullstack.source.LabelSource
import com.nexters.fullstack.ui.holder.MyLabelViewHolder
import com.nexters.fullstack.ui.holder.SearchAddLabelViewHolder
import com.nexters.fullstack.ui.holder.TitleViewHolder
import com.nexters.fullstack.viewmodel.LabelOutAppViewModel

class OutAppLabelAdapter(state : LabelOutAppViewModel.ViewState) : BaseAdapter<LabelSource>() {
    var text = when (state){
        LabelOutAppViewModel.ViewState.MY_LABEL -> MY_LABEL_TITLE
        LabelOutAppViewModel.ViewState.RECENT_LABEL -> RECENT_SEARCH_TITLE
        LabelOutAppViewModel.ViewState.SEARCH_RESULT -> SEARCH_RESULT_TITLE
        LabelOutAppViewModel.ViewState.NO_RESULT -> NO_SEARCH_RESULT
        else -> ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType){
            2002 -> SearchAddLabelViewHolder(
                ItemSearchAddBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
            TITLE -> TitleViewHolder(
                ItemTitleCountBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                ),
                text
            )
            LabelSource.DEFAULT -> MyLabelViewHolder(
                ItemLabelBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    )
                )
            )
            else -> throw NotFoundViewType
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MyLabelViewHolder -> {
                holder.bind(items[position-1])
                holder.itemView.setOnClickListener {
                    getItemClickListener()?.invoke(
                        it,
                        holder.adapterPosition-1,
                        items[holder.adapterPosition-1]
                    )
                }
            }
            is TitleViewHolder -> {
                holder.bind()
            }
            is SearchAddLabelViewHolder -> {
                holder.bind(items[position-1])
                holder.itemView.setOnClickListener {
                    getItemClickListener()?.invoke(
                        it,
                        holder.adapterPosition-1,
                        items[holder.adapterPosition-1]
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int) : Int {
        return if(position == 0) TITLE
        else items[position-1].type
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }


    companion object{
        const val TITLE = 2000

        const val MY_LABEL_TITLE = "내 라벨"
        const val RECENT_SEARCH_TITLE = "최근 검색한 라벨"
        const val SEARCH_RESULT_TITLE = "검색결과"
        const val NO_SEARCH_RESULT = "검색 결과가 없습니다."
    }
}