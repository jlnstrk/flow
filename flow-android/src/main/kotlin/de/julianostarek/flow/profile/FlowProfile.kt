package de.julianostarek.flow.profile

import de.julianostarek.flow.R

val FlowProfile.themeRes: Int get() = when (this) {
    FlowProfile.MUNICH,
    FlowProfile.MUNICH2 -> R.style.Theme_Sequence_Munich
    FlowProfile.STUTTGART -> R.style.Theme_Sequence_Stuttgart
    FlowProfile.FRANKFURT -> R.style.Theme_Sequence_Frankfurt
    FlowProfile.BERLIN -> R.style.Theme_Sequence_Berlin
    FlowProfile.HAMBURG -> R.style.Theme_Sequence_Hamburg
    FlowProfile.ZURICH -> R.style.Theme_Sequence_Zurich
    FlowProfile.VIENNA -> R.style.Theme_Sequence_Vienna
    FlowProfile.RHEINRUHR -> R.style.Theme_Sequence_RheinRuhr
}