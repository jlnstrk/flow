package de.julianostarek.flow.ui.common.backdrop

interface ContentLayerSceneCallbacks {

    fun onContentLayerShiftChanged(isShifted: Boolean) = Unit

    fun onContentLayerOffsetChanged(offset: Int) = Unit

}