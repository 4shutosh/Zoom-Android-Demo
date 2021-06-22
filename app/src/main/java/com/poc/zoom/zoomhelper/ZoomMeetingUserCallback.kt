package com.poc.zoom.zoomhelper

import android.view.SurfaceHolder
import com.poc.zoom.utils.BaseCallback
import com.poc.zoom.utils.BaseEvent
import us.zoom.sdk.*

// inject this? if want a single instance all around
// a class to implement many zoom sdk interfaces into one which is used in the fragment/activity

class ZoomMeetingUserCallback : BaseCallback<ZoomMeetingUserCallback.ZoomBaseMeetingEvent>() {

    interface ZoomBaseMeetingEvent : BaseEvent {
        fun onMeetingStatusChanged(meeting: MeetingStatus, errorCode: Int, internalErrorCode: Int)
        fun onMeetingRequiresPassword(
            required: Boolean,
            needDisplayName: Boolean,
            handler: InMeetingEventHandler
        )

        fun onShareActiveUser(userId: Long)
        fun onCurrentUserMicStatusChanged()
        fun onCurrentUserVideoStatusChanged()
        fun onHostAskMicUnMute()
        fun onHostAskVideoUnMute()
        fun onReceiveChatMessage(receivedMessage: InMeetingChatMessage)
    }

    private var zoomSDKInstance: ZoomSDK = ZoomSDK.getInstance()

    // sub controllers
//    private var inMeetingService = zoomSDKInstance.inMeetingService
//    private var inMeetingShareController = zoomSDKInstance.inMeetingService.inMeetingShareController

    init {
        assignListeners()
    }

    private fun assignListeners() {
        commonListener()
        serviceListener()
        shareListener()
    }

    private fun commonListener() {
        // listener for all inside a meeting events
        val commonListener = object : InMeetingServiceListener {
            override fun onMeetingNeedPasswordOrDisplayName(
                p0: Boolean,
                p1: Boolean,
                p2: InMeetingEventHandler?
            ) {
                for (event in callbacks) {
                    p2?.let { event.onMeetingRequiresPassword(p0, p1, it) }
                }
            }

            override fun onWebinarNeedRegister() {
            }

            override fun onJoinWebinarNeedUserNameAndEmail(p0: InMeetingEventHandler?) {
            }

            override fun onMeetingNeedColseOtherMeeting(p0: InMeetingEventHandler?) {
            }

            override fun onMeetingFail(p0: Int, p1: Int) {
            }

            override fun onMeetingLeaveComplete(p0: Long) {
            }

            override fun onMeetingUserJoin(p0: MutableList<Long>?) {
            }

            override fun onMeetingUserLeave(p0: MutableList<Long>?) {
            }

            override fun onMeetingUserUpdated(p0: Long) {
            }

            override fun onMeetingHostChanged(p0: Long) {
            }

            override fun onMeetingCoHostChanged(p0: Long) {
            }

            override fun onActiveVideoUserChanged(userId: Long) {
            }

            override fun onActiveSpeakerVideoUserChanged(p0: Long) {
            }

            override fun onSpotlightVideoChanged(p0: Boolean) {
            }

            override fun onUserVideoStatusChanged(userId: Long) {
                if (userId == zoomSDKInstance.inMeetingService.myUserID) {
                    for (event in callbacks) {
                        event.onCurrentUserVideoStatusChanged()
                    }
                }
            }

            override fun onUserVideoStatusChanged(
                p0: Long,
                p1: InMeetingServiceListener.VideoStatus?
            ) {
            }

            override fun onUserNetworkQualityChanged(p0: Long) {
            }

            override fun onMicrophoneStatusError(p0: InMeetingAudioController.MobileRTCMicrophoneError?) {
            }

            override fun onUserAudioStatusChanged(userId: Long) {
                if (userId == zoomSDKInstance.inMeetingService.myUserID) {
                    for (event in callbacks) {
                        event.onCurrentUserMicStatusChanged()
                    }
                }
            }

            override fun onUserAudioStatusChanged(
                p0: Long,
                p1: InMeetingServiceListener.AudioStatus?
            ) {
            }

            override fun onHostAskUnMute(userId: Long) {
                if (userId == zoomSDKInstance.inMeetingService.myUserID) {
                    for (event in callbacks) {
                        event.onHostAskMicUnMute()
                    }
                }
            }

            override fun onHostAskStartVideo(userId: Long) {
                if (userId == zoomSDKInstance.inMeetingService.myUserID) {
                    for (event in callbacks) {
                        event.onHostAskVideoUnMute()
                    }
                }
            }

            override fun onUserAudioTypeChanged(p0: Long) {
            }

            override fun onMyAudioSourceTypeChanged(p0: Int) {
            }

            override fun onLowOrRaiseHandStatusChanged(p0: Long, p1: Boolean) {
            }

            override fun onMeetingSecureKeyNotification(p0: ByteArray?) {
            }

            override fun onChatMessageReceived(message: InMeetingChatMessage?) {
                message?.let {
                    for (event in callbacks) {
                        event.onReceiveChatMessage(it)
                    }
                }
            }

            override fun onSilentModeChanged(p0: Boolean) {
            }

            override fun onFreeMeetingReminder(p0: Boolean, p1: Boolean, p2: Boolean) {
            }

            override fun onMeetingActiveVideo(p0: Long) {
            }

            override fun onSinkAttendeeChatPriviledgeChanged(p0: Int) {
            }

            override fun onSinkAllowAttendeeChatNotification(p0: Int) {
            }

            override fun onUserNameChanged(p0: Long, p1: String?) {
            }

            override fun onFreeMeetingNeedToUpgrade(p0: FreeMeetingNeedUpgradeType?, p1: String?) {
            }

            override fun onFreeMeetingUpgradeToGiftFreeTrialStart() {
            }

            override fun onFreeMeetingUpgradeToGiftFreeTrialStop() {
            }

            override fun onFreeMeetingUpgradeToProMeeting() {
            }

            override fun onClosedCaptionReceived(p0: String?) {
            }

            override fun onRecordingStatus(p0: InMeetingServiceListener.RecordingStatus?) {
            }
        }
        zoomSDKInstance.inMeetingService.addListener(commonListener)
    }

    private fun serviceListener() {
        // listener for meeting in/out listener
        val serviceListener =
            MeetingServiceListener { meetingStatus, errorCode, internalErrorCode ->
                for (event in callbacks) {
                    event.onMeetingStatusChanged(meetingStatus, errorCode, internalErrorCode)
                }
            }
        zoomSDKInstance.meetingService.addListener(serviceListener)
    }

    private fun shareListener() {
        val inMeetingShareListener = object : InMeetingShareController.InMeetingShareListener {
            override fun onShareActiveUser(userId: Long) {
                for (event in callbacks) {
                    event.onShareActiveUser(userId)
                }
            }

            override fun onShareUserReceivingStatus(p0: Long) {

            }
        }
        zoomSDKInstance.inMeetingService.inMeetingShareController.addListener(inMeetingShareListener)
    }

    // handled already in MeetingOptions
    private fun videoListeners() {
        val videoListener = object : InMeetingVideoController {
            override fun activeVideoUserID(): Long {
                TODO("Not yet implemented")
            }

            override fun isMyVideoMuted(): Boolean {
                TODO("Not yet implemented")
            }

            override fun canUnmuteMyVideo(): Boolean {
                TODO("Not yet implemented")
            }

            override fun muteMyVideo(p0: Boolean): MobileRTCSDKError {
                TODO("Not yet implemented")
            }

            override fun setVideoCaptureSurfaceHolder(p0: SurfaceHolder?) {
                TODO("Not yet implemented")
            }

            override fun rotateMyVideo(p0: Int): Boolean {
                TODO("Not yet implemented")
            }

            override fun canSwitchCamera(): Boolean {
                TODO("Not yet implemented")
            }

            override fun switchToNextCamera(): Boolean {
                TODO("Not yet implemented")
            }

            override fun switchCamera(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun getCameraDeviceList(): MutableList<CameraDevice> {
                TODO("Not yet implemented")
            }

            override fun getSelectedCameraId(): String {
                TODO("Not yet implemented")
            }

            override fun isFrontCamera(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun isBackCamera(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun stopAttendeeVideo(p0: Long): MobileRTCSDKError {
                TODO("Not yet implemented")
            }

            override fun askAttendeeStartVideo(p0: Long): MobileRTCSDKError {
                TODO("Not yet implemented")
            }

            override fun isUserVideoSpotLighted(p0: Long): Boolean {
                TODO("Not yet implemented")
            }

            override fun spotLightVideo(p0: Boolean, p1: Long): MobileRTCSDKError {
                TODO("Not yet implemented")
            }

            override fun getPinnedUser(): Long {
                TODO("Not yet implemented")
            }

            override fun isUserPinned(p0: Long): Boolean {
                TODO("Not yet implemented")
            }

            override fun pinVideo(p0: Boolean, p1: Long): MobileRTCSDKError {
                TODO("Not yet implemented")
            }

        }
    }

    // handled already in MeetingOptions
    private fun audioListeners() {

        val inMeetingAudioController = zoomSDKInstance.inMeetingService.inMeetingAudioController
        val audioListener = object : InMeetingAudioController {
            override fun isMyAudioMuted(): Boolean {
                return inMeetingAudioController.isAudioConnected
            }

            override fun canUnmuteMyAudio(): Boolean {
                return inMeetingAudioController.canUnmuteMyAudio()
            }

            override fun muteMyAudio(mute: Boolean): MobileRTCSDKError {
                return inMeetingAudioController.muteMyAudio(mute)
            }

            override fun isAudioConnected(): Boolean {
                return inMeetingAudioController.isAudioConnected
            }

            override fun disconnectAudio(): MobileRTCSDKError {
                return inMeetingAudioController.disconnectAudio()
            }

            override fun connectAudioWithVoIP(): MobileRTCSDKError {
                return inMeetingAudioController.connectAudioWithVoIP()
            }

            override fun canSwitchAudioOutput(): Boolean {
                return inMeetingAudioController.canSwitchAudioOutput()
            }

            override fun setLoudSpeakerStatus(loudSpeaker: Boolean): MobileRTCSDKError {
                return inMeetingAudioController.setLoudSpeakerStatus(loudSpeaker)
            }

            override fun getLoudSpeakerStatus(): Boolean {
                return inMeetingAudioController.loudSpeakerStatus
            }

            override fun muteAttendeeAudio(mute: Boolean, userId: Long): MobileRTCSDKError {
                return inMeetingAudioController.muteAttendeeAudio(mute, userId)
            }

            override fun muteAllAttendeeAudio(muteAll: Boolean): MobileRTCSDKError {
                return inMeetingAudioController.muteAllAttendeeAudio(muteAll)
            }

            override fun unmuteAllAttendeeAudio(): MobileRTCSDKError {
                return inMeetingAudioController.unmuteAllAttendeeAudio()
            }

            override fun setMuteOnEntry(mute: Boolean): MobileRTCSDKError {
                return setMuteOnEntry(mute)
            }

            override fun isMuteOnEntryOn(): Boolean {
                return isMuteOnEntryOn
            }
        }
    }


    private fun qaListener() {
        val qaListener = object : InMeetingQAController.InMeetingQAListener {
            override fun onQAConnectStarted() {

            }

            override fun onAllowAskQuestionAnonymousStatus(p0: Boolean) {

            }

            override fun onAllowAttendeeViewAllQuestionStatus(p0: Boolean) {

            }

            override fun onAllowAttendeeVoteupQuestionStatus(p0: Boolean) {

            }

            override fun onAllowAttendeeCommentQuestionStatus(p0: Boolean) {
            }

            override fun onQAConnected(p0: Boolean) {
            }

            override fun onAddQuestion(p0: String?, p1: Boolean) {

            }

            override fun onAddAnswer(p0: String?, p1: Boolean) {
            }

            override fun onReceiveQuestion(p0: String?) {
            }

            override fun onReceiveAnswer(p0: String?) {
            }

            override fun onQuestionMarkedAsDismissed(p0: String?) {
            }

            override fun onReopenQuestion(p0: String?) {
            }

            override fun onUserLivingReply(p0: String?) {
            }

            override fun onUserEndLiving(p0: String?) {
            }

            override fun onUpvoteQuestion(p0: String?, p1: Boolean) {
            }

            override fun onRevokeUpvoteQuestion(p0: String?, p1: Boolean) {
            }

            override fun onDeleteQuestion(p0: MutableList<String>?) {
            }

            override fun onDeleteAnswer(p0: MutableList<String>?) {
            }

        }
        zoomSDKInstance.inMeetingService.inMeetingQAController.addQAListener(qaListener)
    }

}