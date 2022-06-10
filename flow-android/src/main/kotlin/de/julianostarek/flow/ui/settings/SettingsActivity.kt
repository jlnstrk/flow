package de.julianostarek.flow.ui.settings

import android.os.Bundle
import android.view.View
import de.julianostarek.flow.databinding.ActivitySettingsBinding
import de.julianostarek.flow.ui.common.activity.SequenceActivity

class SettingsActivity : SequenceActivity(), View.OnClickListener {
    private lateinit var viewBinding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.toolbar.setNavigationOnClickListener(this)
        attachPreferences()
    }

    private fun attachPreferences() {
        supportFragmentManager.beginTransaction()
            .replace(viewBinding.fragmentContainer.id, SettingsFragment())
            .commit()
    }

    override fun onClick(view: View) {
        when (view.id) {
            else -> finish()
        }
    }

}