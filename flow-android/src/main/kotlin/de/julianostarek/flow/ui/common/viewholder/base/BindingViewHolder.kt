package de.julianostarek.flow.ui.common.viewholder.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class BindingViewHolder<T, VB : ViewBinding>(
    parent: ViewGroup,
    inflate: BindingInflate<VB>,
    val viewBinding: VB = inflate.invoke(LayoutInflater.from(parent.context), parent, false)
) : BaseViewHolder<T>(viewBinding.root)