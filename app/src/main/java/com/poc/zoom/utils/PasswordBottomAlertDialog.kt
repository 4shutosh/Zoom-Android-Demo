package com.poc.zoom.utils

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.poc.zoom.databinding.LayoutBottomSheetPasswordBinding

class PasswordBottomAlertDialog constructor(
    mContext: Context,
    title: String,
    body: String,
    positiveButtonText: String,
) : BottomSheetDialog(mContext) {

    private var binding: LayoutBottomSheetPasswordBinding =
        LayoutBottomSheetPasswordBinding.inflate(LayoutInflater.from(context))
    var positiveButton: MaterialButton

    init {
        val titleTv = binding.alertHeading
        val bodyTv = binding.alertBody
        positiveButton = binding.okayButton

        titleTv.text = title
        bodyTv.text = body
        positiveButton.text = positiveButtonText
        setContentView(binding.root)
        setCancelable(false)
    }

    fun getPassword(): String {
        return binding.passwordEt.text.toString()
    }


}
