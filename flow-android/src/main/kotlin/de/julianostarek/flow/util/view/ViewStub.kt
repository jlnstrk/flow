package de.julianostarek.flow.util.view

import android.view.View
import android.view.ViewGroup
import android.view.ViewStub

fun ViewStub.deflate(view: View): ViewStub {
    val viewParent = view.parent

    if (viewParent != null && viewParent is ViewGroup) {
        val index = viewParent.indexOfChild(view)
        viewParent.removeView(view)
        val viewStub = ViewStub(context).apply {
            id = this@deflate.id
            inflatedId = view.id
            layoutParams = view.layoutParams
        }
        viewParent.addView(viewStub, index)
        return viewStub
    } else {
        throw IllegalStateException("Inflated view doesn't have a parent")
    }
}