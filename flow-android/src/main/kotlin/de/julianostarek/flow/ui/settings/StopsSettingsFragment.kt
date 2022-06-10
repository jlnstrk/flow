package de.julianostarek.flow.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.julianostarek.flow.R

class StopsSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_stops, rootKey)
    }

}