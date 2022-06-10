package de.julianostarek.flow.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.profileselector.NetworkProfileSelectorDialog
import de.julianostarek.flow.util.context.profile

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val networkProfile = findPreference<Preference>("network_profile")
        networkProfile?.setSummaryProvider {
            requireActivity().profile.name
        }
        networkProfile?.setOnPreferenceClickListener {
            NetworkProfileSelectorDialog()
                .show(requireActivity().supportFragmentManager, null)
            return@setOnPreferenceClickListener true
        }

        val darkMode = findPreference<DropDownPreference>("dark_mode")
        darkMode?.setOnPreferenceChangeListener { preference, newValue ->
            AppCompatDelegate.setDefaultNightMode((newValue as String).toInt())
            true
        }
    }

    companion object {
        private const val ORDER_EXPAND_BUTTON = 999
    }

}