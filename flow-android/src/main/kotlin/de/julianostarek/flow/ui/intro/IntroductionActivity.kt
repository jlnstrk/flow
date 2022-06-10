package de.julianostarek.flow.ui.intro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import de.julianostarek.flow.R
import de.julianostarek.flow.ui.common.activity.SequenceActivity

class IntroductionActivity : SequenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.ThemeOverlay_Sequence_Dark)
        setTheme(R.style.ThemeOverlay_Sequence_BackdropActivity)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_LOCATION_PERMISSION = 1
    }

}