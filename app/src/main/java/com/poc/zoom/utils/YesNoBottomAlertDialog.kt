package com.poc.zoom.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.poc.zoom.R
import com.poc.zoom.databinding.LayoutBottomAlertYesNoBinding

class YesNoBottomAlertDialog constructor(
    mContext: Context,
    title: String,
    body: String,
    positiveButtonText: String,
) : BottomSheetDialog(mContext) {

    private var binding: LayoutBottomAlertYesNoBinding =
        LayoutBottomAlertYesNoBinding.inflate(LayoutInflater.from(context))
    var positiveButton: MaterialButton

    init {
        val titleTv = binding.alertHeading
        val bodyTv = binding.alertBody
        positiveButton = binding.positiveButton

        titleTv.text = title
        bodyTv.text = body
        positiveButton.text = positiveButtonText

        binding.negativeButton.setOnClickListener {
            dismiss()
        }
        setContentView(binding.root)
    }

}