package de.julianostarek.flow.ui.main.map

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.fragment.app.Fragment

class MapContentFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as ViewGroup).getChildAt(0).apply {
            outlineProvider = ViewOutlineProvider.BACKGROUND
            clipToOutline = true
            (this as ViewGroup).clipChildren = true
        }
    }

}