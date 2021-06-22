package com.poc.zoom.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.poc.zoom.databinding.LayoutMeetingOptionsBinding
import us.zoom.sdk.InMeetingAudioController
import us.zoom.sdk.InMeetingVideoController
import us.zoom.sdk.ZoomSDK

// todo make the icon change after the audio state is changed using the InMeetingService not using the selector ::getInMeetingAudioController
class MeetingOptions @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    private lateinit var chatIconClickListener: ChatIconClickListener

    private var binding: LayoutMeetingOptionsBinding

    private var zoomSDKInstance: ZoomSDK
    private val inMeetingAudioController: InMeetingAudioController
    private val inMeetingVideoController: InMeetingVideoController

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = LayoutMeetingOptionsBinding.inflate(inflater, this, true)

        zoomSDKInstance = ZoomSDK.getInstance()
        inMeetingAudioController = zoomSDKInstance.inMeetingService.inMeetingAudioController
        inMeetingVideoController = zoomSDKInstance.inMeetingService.inMeetingVideoController

        setUpView()
//        attachListeners()
    }

    private fun setUpView() {

        binding.meetingOptionsMic.isActivated = inMeetingAudioController.isMyAudioMuted

        binding.meetingOptionsMic.setOnClickListener {
            switchMic()
        }
        binding.meetingOptionsVideo.setOnClickListener {
            switchVideo()
        }
        binding.meetingOptionsChat.setOnClickListener {
            if (this::chatIconClickListener.isInitialized) {
                chatIconClickListener.onChatIconClick()
                switchChatIcon()
            }
        }
    }

//    private fun attachListeners() {
//        audioListener()
//    }

    fun updateAllViews() {
        binding.meetingOptionsMic.isActivated = inMeetingAudioController.isMyAudioMuted
        binding.meetingOptionsVideo.isActivated = inMeetingVideoController.isMyVideoMuted
    }

    fun updateAudioButton() {
        binding.meetingOptionsMic.isActivated = inMeetingAudioController.isMyAudioMuted
    }

    fun updateVideoButton() {
        binding.meetingOptionsMic.isActivated = inMeetingAudioController.isMyAudioMuted
    }

    fun switchMic() {
        if (inMeetingAudioController.isAudioConnected) {
            inMeetingAudioController.muteMyAudio(!inMeetingAudioController.isMyAudioMuted)
            binding.meetingOptionsMic.isActivated = inMeetingAudioController.isMyAudioMuted
        }
    }

    fun switchVideo() {
        // help check for permissions
        inMeetingVideoController.muteMyVideo(!inMeetingVideoController.isMyVideoMuted)
        binding.meetingOptionsVideo.isActivated = inMeetingVideoController.isMyVideoMuted
    }

    private fun switchChatIcon() {
        binding.meetingOptionsChat.isActivated = !binding.meetingOptionsChat.isActivated
    }

    fun setChatIconClickListener(listener: ChatIconClickListener) {
        chatIconClickListener = listener
    }

}

interface ChatIconClickListener {
    fun onChatIconClick()
}