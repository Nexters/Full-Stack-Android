package com.nexters.feature.ui.layout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.nexters.feature.BR
import com.nexters.feature.R
import com.nexters.feature.databinding.ItemPalletViewBinding
import com.nexters.feature.ui.data.PalletItem
import com.nexters.feature.util.ColorUtils
import com.nexters.feature.viewmodel.PalletViewModel

class PalletGridLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defaultAttr: Int = 0
) : GridLayout(context, attrs, defaultAttr), LifecycleObserver, LifecycleOwner,
    ViewModelStoreOwner {

    private val _items = mutableListOf<PalletItem>()

    lateinit var setOnLabelClickListener: (PalletItem) -> Unit

    private val lifecycleRegister = LifecycleRegistry(this)

    private val viewModelStore = ViewModelStore()

    var setItems: List<PalletItem>
        set(value) {
            if (value.isNotEmpty()) {
                _items.addAll(value)
            }
        }
        get() = _items

    lateinit var selectedView: View

    lateinit var selectedPalletItem: PalletItem

    init {
        columnCount = 2
    }

    fun setOnInitView() {
        _items.forEachIndexed { index, item ->
            val childView = DataBindingUtil.bind<ItemPalletViewBinding>(
                LayoutInflater.from(context).inflate(R.layout.item_pallet_view, this, false)
            )

            val viewModel = ViewModelProvider(this).get(PalletViewModel::class.java)
            childView?.lifecycleOwner = this
            childView?.setVariable(BR.vm, viewModel)
            childView?.executePendingBindings()
            
            val textView = childView?.label

            if (textView != null) {
                if (index % 2 == 0) {
                    textView.width =
                        (resources.displayMetrics.widthPixels / 2) - textView.marginRight - (textView.marginLeft * 2)
                } else {
                    textView.width =
                        (resources.displayMetrics.widthPixels / 2) - textView.marginRight - textView.marginLeft
                }
            }


            textView?.run {
                text = item.name

                setBackgroundColor(ColorUtils(item.name, context).getInactive())
                setTextColor(ColorUtils(item.name, context).getText())
                setOnClickListener { itemView ->
                    if (::selectedView.isInitialized) {
                        selectedView.isSelected = false
                        selectedView.setBackgroundColor(
                            ColorUtils(
                                selectedPalletItem.name,
                                context
                            ).getInactive()
                        )
                    }

                    selectedPalletItem = item
                    selectedView = itemView


                    itemView.isSelected = true

                    setBackgroundColor(ColorUtils(item.name, context).getActive())
                    setOnLabelClickListener(item)

                    invalidate()
                }
            }

            addView(childView?.root)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onStartEvent(event: Lifecycle.Event) {
        lifecycleRegister.handleLifecycleEvent(event)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegister
    }

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }
}