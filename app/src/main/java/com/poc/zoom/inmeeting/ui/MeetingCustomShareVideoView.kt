package com.poc.zoom.inmeeting.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.poc.zoom.databinding.LayoutMeetingViewShareBinding

class MeetingCustomShareVideoView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    var binding: LayoutMeetingViewShareBinding

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = LayoutMeetingViewShareBinding.inflate(inflater, this, true)
    }

}