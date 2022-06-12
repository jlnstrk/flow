package de.julianostarek.flow

import android.app.Application
import android.graphics.Typeface
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.google.android.material.resources.TextAppearance
import com.google.firebase.FirebaseApp
import de.julianostarek.flow.profile.FlowProfile
import de.julianostarek.flow.profile.themeRes

class FlowApp : Application() {
    lateinit var networkProfile: FlowProfile
        private set
    private val listeners: MutableList<OnProviderChangeListener> = ArrayList()

    fun interface OnProviderChangeListener {
        fun onProviderChange(profile: FlowProfile)
    }

    fun onProviderChange(listener: OnProviderChangeListener) {
        listeners.add(listener)
        listener.onProviderChange(this.networkProfile)
    }

    override fun onCreate() {
        super.onCreate()
        initKoin(this)
        FirebaseApp.initializeApp(this)

        val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val defaultNightMode = defaultPreferences
            .getString("dark_mode", AppCompatDelegate.MODE_NIGHT_NO.toString())!!.toInt()
        AppCompatDelegate.setDefaultNightMode(defaultNightMode)

        val networkProfile = defaultPreferences
            .getString("network_profile", FlowProfile.MUNICH2.name)!!
        this.networkProfile = FlowProfile.valueOf(networkProfile)
        adjustTypefaceDefaults()

//        StrictMode.setThreadPolicy(
//            ThreadPolicy.Builder()
//                .detectAll()
//                .penaltyLog()
//                .penaltyFlashScreen()
//                .build()
//        )
    }

    fun switchProfile(profile: FlowProfile): Boolean {
        if (this.networkProfile == profile) {
            return false
        }
        this.networkProfile = profile
        adjustTypefaceDefaults()
        listeners.forEach {
            it.onProviderChange(profile)
        }
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putString("network_profile", profile.name)
            .apply()
        return true
    }

    @Suppress("RestrictedApi")
    private fun adjustTypefaceDefaults() {
        val def = Typeface::class.java.getDeclaredField("DEFAULT")
        val defBold = Typeface::class.java.getDeclaredField("DEFAULT_BOLD")
        val defs = Typeface::class.java.getDeclaredField("sDefaults")
        def.isAccessible = true
        defBold.isAccessible = true
        defs.isAccessible = true

        val profileContext = ContextThemeWrapper(this, networkProfile.themeRes)
        val attrs = profileContext.obtainStyledAttributes(intArrayOf(R.attr.textAppearanceHeadline6, R.attr.textAppearanceSubtitle1))
        val headline6Res = attrs.getResourceId(0, R.style.TextAppearance_Sequence_Headline6)
        val subtitle1Res = attrs.getResourceId(1, R.style.TextAppearance_Sequence_Subtitle1)
        attrs.recycle()

        val headline6 = TextAppearance(this, headline6Res)
        val subtitle1 = TextAppearance(this, subtitle1Res)

        val semiBold = subtitle1.getFont(this) // ResourcesCompat.getFont(this, R.font.gr_semibold)
        // val semiBoldItalic = ResourcesCompat.getFont(this, R.font.gr_semibold_italic)
        val bold = headline6.getFont(this) // ResourcesCompat.getFont(this, R.font.gr_bold)
        // val boldItalic = ResourcesCompat.getFont(this, R.font.gr_bold_italic)
        def.set(null, semiBold)
        defBold.set(null, bold)
        defs.set(null, arrayOf(semiBold, bold))//, semiBoldItalic, boldItalic))
    }

    companion object {
        const val PREF_NAME_STARTUP = "startup"
        const val PREF_NAME_PREFERENCES = "preferences"

        const val PREF_KEY_INTRO = "intro"
        const val PREF_KEY_PROFILE = "profile"
    }

}