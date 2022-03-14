package com.tutuland.dogdroid.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.tutuland.dogdroid.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForceRefreshDialogFragment : DialogFragment() {
    private val viewModel: DogListViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it)
                .setMessage(R.string.force_refresh_message)
                .setPositiveButton(R.string.force_refresh_positive) { _, _ -> viewModel.refreshData() }
                .setNegativeButton(R.string.force_refresh_negative) { _, _ -> }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
