package com.poc.zoom.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.poc.zoom.databinding.LayoutMeetingViewNormalBinding

class MeetingVideoView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    var binding: LayoutMeetingViewNormalBinding

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = LayoutMeetingViewNormalBinding.inflate(inflater, this, true)
    }

}