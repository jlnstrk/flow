package de.julianostarek.flow.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import de.julianostarek.flow.R
import de.julianostarek.flow.FlowApp
import de.julianostarek.flow.ui.intro.IntroductionActivity
import de.julianostarek.flow.ui.common.activity.SequenceActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : SequenceActivity() {
    private var navigationFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.ThemeOverlay_Sequence_BackdropActivity)
        setContentView(R.layout.activity_main)
        lifecycleScope.launchWhenCreated {
            if (!withContext(Dispatchers.IO) { getSharedPreferences(FlowApp.PREF_NAME_STARTUP, Context.MODE_PRIVATE)
                    .getBoolean(FlowApp.PREF_KEY_INTRO, false) }
            ) {
                val intent = Intent(this@MainActivity, IntroductionActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_INTRO)
            } else {
                init(savedInstanceState)
            }
        }
    }

    private fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            navigationFragment = supportFragmentManager.findFragmentByTag("fragment")
            if (navigationFragment != null) {
                return
            }
        }
        displayStartupFragment()
    }

    override fun onBackPressed() {
        if (navigationFragment is MainFragment
            && !(navigationFragment as MainFragment).findContentLayer()?.navigateUp()!!
        ) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == Activity.RESULT_OK) {
                getSharedPreferences(FlowApp.PREF_NAME_STARTUP, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(FlowApp.PREF_KEY_INTRO, true)
                    .apply()
                init(null)
            } else finish()
        }
    }

    private fun displayStartupFragment() {
        navigationFragment = MainFragment()
        replaceFragment()
    }

    private fun replaceFragment() {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.activity_main_fragment_container, navigationFragment!!)
            .commitNow()
    }

    companion object {
        private val REQUEST_CODE_INTRO = IntroductionActivity::class.java.hashCode() shr 16
    }

}
