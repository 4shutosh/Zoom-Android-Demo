package com.poc.zoom.inmeeting.ui

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

    private var binding: LayoutMeetingOptionsBinding

    private lateinit var meetingOptionsChatClickListener: MeetingOptionsChatClickListener

    private var zoomSDKInstance: ZoomSDK
    private lateinit var inMeetingAudioController: InMeetingAudioController
    private lateinit var inMeetingVideoController: InMeetingVideoController

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = LayoutMeetingOptionsBinding.inflate(inflater, this, true)

        zoomSDKInstance = ZoomSDK.getInstance()
        if (zoomSDKInstance.inMeetingService != null) {
            inMeetingAudioController = zoomSDKInstance.inMeetingService.inMeetingAudioController
            inMeetingVideoController = zoomSDKInstance.inMeetingService.inMeetingVideoController
        }

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
            if (this::meetingOptionsChatClickListener.isInitialized) {
                meetingOptionsChatClickListener.onChatIconClick()
                switchChatIcon()
            }
        }
        binding.meetingOptionsWeb.setOnClickListener {
            if (this::meetingOptionsChatClickListener.isInitialized) {
                meetingOptionsChatClickListener.onWebIconClick()
            }
        }
        binding.meetingOptionsLeaveMeeting.setOnClickListener {
            zoomSDKInstance.inMeetingService.leaveCurrentMeeting(false)
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
        if (!inMeetingVideoController.isMyVideoMuted) {
            inMeetingVideoController.rotateMyVideo(0)
        }
    }

    private fun switchChatIcon() {
        binding.meetingOptionsChat.isActivated = !binding.meetingOptionsChat.isActivated
    }

    fun setChatIconClickListener(listener: MeetingOptionsChatClickListener) {
        meetingOptionsChatClickListener = listener
    }

    fun setWebIconClickListener(listener: MeetingOptionsChatClickListener) {
        meetingOptionsChatClickListener = listener
    }

}

interface MeetingOptionsChatClickListener {
    fun onChatIconClick()
    fun onWebIconClick()
}