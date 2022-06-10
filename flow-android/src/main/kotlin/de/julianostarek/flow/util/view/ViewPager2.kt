package de.julianostarek.flow.util.view

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

inline val ViewPager2.recyclerView: RecyclerView
    get() = getChildAt(0) as RecyclerView