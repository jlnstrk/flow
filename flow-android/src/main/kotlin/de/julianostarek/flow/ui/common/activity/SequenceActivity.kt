package de.julianostarek.flow.ui.common.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.julianostarek.flow.FlowApp
import de.julianostarek.flow.profile.themeRes

abstract class SequenceActivity : AppCompatActivity() {
    val app: FlowApp
        get() = application as FlowApp

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(app.networkProfile.themeRes)
        super.onCreate(savedInstanceState)
    }

}