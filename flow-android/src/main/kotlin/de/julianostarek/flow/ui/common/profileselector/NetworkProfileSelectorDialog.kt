package de.julianostarek.flow.ui.common.profileselector

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.julianostarek.flow.FlowApp
import de.julianostarek.flow.databinding.DialogNetworkProfileChooserBinding
import de.julianostarek.flow.util.context.profile

class NetworkProfileSelectorDialog : DialogFragment(), View.OnClickListener {
    private lateinit var viewBinding: DialogNetworkProfileChooserBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewBinding = DialogNetworkProfileChooserBinding.inflate(layoutInflater)
        viewBinding.apply.setOnClickListener(this)
        viewBinding.pager.currentSelection = requireActivity().profile

        return MaterialAlertDialogBuilder(requireContext())
            .setView(viewBinding.root)
            .create()
    }

    override fun onClick(view: View) {
        when (view.id) {
            viewBinding.apply.id -> {
                dismiss()
                val selected = viewBinding.pager.currentSelection
                val activity = requireActivity()
                if ((activity.application as FlowApp)
                    .switchProfile(selected)) {
                    val intent = activity.intent
                    activity.finish()
                    activity.startActivity(intent)
                    activity.overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                }
            }
        }
    }

}