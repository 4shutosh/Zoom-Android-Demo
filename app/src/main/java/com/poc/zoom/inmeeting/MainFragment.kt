package com.poc.zoom.inmeeting

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.poc.zoom.R
import com.poc.zoom.databinding.FragmentMainBinding
import com.poc.zoom.inmeeting.adapter.ChatAdapter
import com.poc.zoom.inmeeting.ui.MeetingOptionsChatClickListener
import com.poc.zoom.inmeeting.ui.MeetingVideoView
import com.poc.zoom.inmeeting.viewmodel.MainFragmentViewModel
import com.poc.zoom.inmeeting.viewmodel.MainFragmentViewModel.Companion.LAYOUT_TYPE_IN_WAIT_ROOM
import com.poc.zoom.inmeeting.viewmodel.MainFragmentViewModel.Companion.LAYOUT_TYPE_LIST_VIDEO
import com.poc.zoom.inmeeting.viewmodel.MainFragmentViewModel.Companion.LAYOUT_TYPE_OTHER_SHARE
import com.poc.zoom.inmeeting.viewmodel.MainFragmentViewModel.Companion.LAYOUT_TYPE_WAITHOST
import com.poc.zoom.inmeeting.viewmodel.MainFragmentViewModel.Companion.LAYOUT_TYPE_WEBINAR_ATTENDEE
import com.poc.zoom.inmeeting.zoomhelper.ZoomMeetingUserCallback
import com.poc.zoom.utils.AnimationHelper.enterToRight
import com.poc.zoom.utils.AnimationHelper.exitToLeft
import com.poc.zoom.utils.PasswordBottomAlertDialog
import com.poc.zoom.utils.YesNoBottomAlertDialog
import com.poc.zoom.utils.onRightDrawableClicked
import dagger.hilt.android.AndroidEntryPoint
import us.zoom.sdk.*
import javax.inject.Inject


// inflate the video view once the sdk is ready to show the video or else it will crash
// todo : Need to create layouts for each screen meeting type : Video, ShareScreen, updateUserVideo listener

/*
* help custom share layout meaning -> drawing board*/

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main),
    ZoomMeetingUserCallback.ZoomBaseMeetingEvent {

    @Inject
    lateinit var zoomSDKInstance: ZoomSDK

    private lateinit var defaultVideoManager: MobileRTCVideoViewManager

    private val viewModel: MainFragmentViewModel by viewModels()

    private lateinit var binding: FragmentMainBinding
    private val args: MainFragmentArgs by navArgs()

    private val chatAdapter: ChatAdapter by lazy { ChatAdapter() }
    private val chatList = mutableListOf<InMeetingChatMessage>()

    private var userLeave: Boolean = false

    // meeting variables
    private var currentVideoLayoutType = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        setUpView()
        initLiveData()
    }

    override fun onResume() {
        super.onResume()
        checkForZoomLayouts()
    }

    private fun checkForZoomLayouts() {
//        initializeVideoView()
    }

    private fun setUpView() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.viewModel = this.viewModel
        checkPermissions()

        setUpBackButton()

        binding.fragmentMainChatLayout.chatLayoutRecyclerView.adapter = chatAdapter
        chatAdapter.submitList(chatList)

        val chatIconClickListener = object : MeetingOptionsChatClickListener {
            // add animation here
            override fun onChatIconClick() {
                if (binding.fragmentMainChatLayout.root.isVisible) {
                    binding.fragmentMainChatLayout.root.exitToLeft()
                } else {
                    binding.fragmentMainChatLayout.root.enterToRight()
                }
            }

            override fun onWebIconClick() {
                viewModel.actionWebIconClick()
            }

            override fun onLeaveMeetingIconClick() {
                activity?.onBackPressed()
            }
        }
        binding.fragmentMainMeetingOptions.setChatIconClickListener(chatIconClickListener)

        binding.fragmentMainChatLayout.chatLayoutInput.onRightDrawableClicked {
            val message = binding.fragmentMainChatLayout.chatLayoutInput.text?.trim().toString()
            if (message.isNotEmpty()) {
                zoomSDKInstance.inMeetingService.inMeetingChatController.sendChatToGroup(
                    InMeetingChatController.MobileRTCChatGroup.MobileRTCChatGroup_All,
                    message
                )
                binding.fragmentMainChatLayout.chatLayoutInput.text?.clear()
                view?.let {
                    val imm =
                        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                }
            }
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val exitDialog = YesNoBottomAlertDialog(
                requireContext(),
                title = resources.getString(R.string.are_you_sure),
                body = resources.getString(R.string.exit_meeting),
                positiveButtonText = resources.getString(R.string.leave)
            )
            exitDialog.positiveButton.setOnClickListener {
                userLeave = true
                viewModel.actionLeaveMeeting()
                exitDialog.dismiss()
                // here false implies: don't end the meeting
                zoomSDKInstance.inMeetingService.leaveCurrentMeeting(false)
            }
            if (exitDialog.isShowing) {
                exitDialog.dismiss()
            } else {
                exitDialog.show()
            }
        }
    }

    private fun setUpBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun initLiveData() {
        val number = args.meetingNumber
        val name = args.userName
        viewModel.joinMeeting(number, name, requireContext())

        viewModel.meetingLayoutType.observe(viewLifecycleOwner) {
            changeVideoLayout(it)
        }
        registerZoomListeners()
        setUpZoomViews()
    }

    private fun questionFeature() {
        // show a dialog layout
        viewModel.startQuestionFeature(zoomSDKInstance.inMeetingService.currentMeetingNumber)
        view?.let {
            viewModel.questionViewState.observe(viewLifecycleOwner) { question ->
                binding.fragmentMainQuestionButton.setOnClickListener {
                    viewModel.actionQuestionButtonAction(
                        args.userName,
                        question
                    )
                }
            }
        }
        viewModel.buttonV.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentMainQuestionButton.visibility = View.VISIBLE
            } else {
                binding.fragmentMainQuestionButton.visibility = View.GONE
            }
        }
    }

    private fun setUpZoomViews() {
        val videoView = MeetingVideoView(requireContext())
        // inflate views only after you've joined the meeting
        binding.fragmentMainVideoView.addView(videoView, MATCH_PARENT, MATCH_PARENT)
        defaultVideoManager = videoView.binding.videoContentView.videoViewManager
    }

    private fun registerZoomListeners() {
        // listeners
        ZoomMeetingUserCallback().addListener(this)
    }

    override fun onMeetingStatusChanged(
        meeting: MeetingStatus,
        errorCode: Int,
        internalErrorCode: Int
    ) {
        Log.d(TAG, "onMeetingStatusChanged: $meeting")
        when (meeting) {
            // handle all cases here
            MeetingStatus.MEETING_STATUS_INMEETING -> {
                view?.post {
                    initializeVideoView()
                }
            }
            MeetingStatus.MEETING_STATUS_CONNECTING -> showConnectingView()
            MeetingStatus.MEETING_STATUS_DISCONNECTING -> if (!userLeave) viewModel.actionLeaveMeeting()
            MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM -> showWaitingLayout()
            else -> {
            }
        }
    }

    private fun showWaitingLayout() {
        binding.fragmentMainWaitingView.visibility = View.VISIBLE
        currentVideoLayoutType = LAYOUT_TYPE_IN_WAIT_ROOM
    }

    private fun hideWaitingLayout() {
        binding.fragmentMainWaitingView.visibility = View.GONE
    }

    override fun onMeetingRequiresPassword(
        required: Boolean,
        needDisplayName: Boolean,
        handler: InMeetingEventHandler
    ) {
        // show passwordDialog
        showPasswordDialog(handler)
    }

    override fun onShareActiveUser(userId: Long) {
        initializeVideoView()
    }

    override fun onCurrentUserMicStatusChanged() {
        binding.fragmentMainMeetingOptions.updateAudioButton()
    }

    override fun onCurrentUserVideoStatusChanged() {
        binding.fragmentMainMeetingOptions.updateVideoButton()
    }

    override fun onHostAskMicUnMute() {
        binding.fragmentMainMeetingOptions.switchMic()
    }

    override fun onHostAskVideoUnMute() {
        binding.fragmentMainMeetingOptions.turnCameraOn()
    }

    override fun onReceiveChatMessage(receivedMessage: InMeetingChatMessage) {
        Log.d(TAG, "onReceiveChatMessage: $receivedMessage")
        chatList.add(receivedMessage)
//        chatAdapter.submitList(chatList)
        chatAdapter.notifyDataSetChanged()
    }

    override fun breakOutSession(iboAttendee: IBOAttendee) {
        iboAttendee.joinBo()
    }

    private fun changeVideoLayout(videoType: Int) {
//        if (videoType == )
    }

    private fun showPasswordDialog(handler: InMeetingEventHandler) {
        val dialog = PasswordBottomAlertDialog(
            requireContext(),
            "Please Enter Password",
            "Please Enter Password",
            "Join"
        )
        dialog.positiveButton.setOnClickListener {
            // join with password
            val password = dialog.getPassword()
            if (password.isNotEmpty()) {
                handler.setMeetingNamePassword(password, args.userName)
            }
            dialog.dismiss()
        }
        activity?.let {
            if (!it.isFinishing)
                dialog.show()
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permRequest.launch(
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.CAMERA
                )
            )
        }
    }

    private val permRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (!it.value)
                    checkPermissions()
            }
        }

    private fun initializeVideoView() {
        hideWaitingLayout()

        binding.fragmentMainMeetingOptions.updateAllViews()
        val videoLayoutType = getNewVideoMeetingLayout()
        Log.d(TAG, "initializeVideoView: found video type $videoLayoutType")
        if (currentVideoLayoutType != videoLayoutType) {
            removeLayout(currentVideoLayoutType)
            currentVideoLayoutType = videoLayoutType
            addLayout(videoLayoutType)
        } else {
            Log.d(TAG, "initializeVideoView: reached same layout")
        }
        showCurrentUserVideoLayout()
        questionFeature()
    }

    private fun removeLayout(videoLayoutType: Int) {

    }


    private fun addLayout(videoLayoutType: Int) {
        // remove initial other view types
        when (videoLayoutType) {
            LAYOUT_TYPE_IN_WAIT_ROOM -> {
            }
            LAYOUT_TYPE_OTHER_SHARE -> {
                Log.d(TAG, "addLayout: type share")
                showShareViewVideoLayout()
            }
            else -> {
                showActiveVideoLayout()
            }
        }
    }

    private fun getNewVideoMeetingLayout(): Int {
        val meetingService = zoomSDKInstance.meetingService
        val inMeetingService = zoomSDKInstance.inMeetingService

        var newLayoutType = -1
        if (meetingService.meetingStatus == MeetingStatus.MEETING_STATUS_WAITINGFORHOST) {
            newLayoutType = LAYOUT_TYPE_WAITHOST
            return newLayoutType
        }
        if (meetingService.meetingStatus == MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM) {
            newLayoutType = LAYOUT_TYPE_IN_WAIT_ROOM
            return newLayoutType
        }
        if (inMeetingService.isWebinarMeeting) {
            if (isMySelfWebinarAttendee()) {
                newLayoutType = LAYOUT_TYPE_WEBINAR_ATTENDEE
                return newLayoutType
            }
        }
        // add share layout check here
        if (isOtherUserSharing()) {
            newLayoutType = LAYOUT_TYPE_OTHER_SHARE
            return newLayoutType
        }
        newLayoutType = LAYOUT_TYPE_LIST_VIDEO
        return newLayoutType
    }

    private fun showPreviewLayout() {
        val previewRenderInfo = MobileRTCVideoUnitRenderInfo(0, 0, 100, 100)
        defaultVideoManager.removeAllVideoUnits()
        defaultVideoManager.addPreviewVideoUnit(previewRenderInfo)
    }

    private fun showCurrentUserVideoLayout() {
        val currentUserVideoView = MeetingVideoView(requireContext())
        binding.fragmentMainUserVideo.addView(currentUserVideoView)
        val currentUserVideoManager = currentUserVideoView.binding.videoContentView.videoViewManager


        val currentUserRender = MobileRTCVideoUnitRenderInfo(0, 0, 100, 100)
        val currentUserId = zoomSDKInstance.inMeetingService.myUserID
        currentUserVideoManager.addAttendeeVideoUnit(currentUserId, currentUserRender)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    private fun showActiveVideoLayout() {

        val activeRenderInfo = MobileRTCVideoUnitRenderInfo(0, 0, 100, 100)
        defaultVideoManager.removeAllVideoUnits()
        val usersIdList = zoomSDKInstance.inMeetingService.inMeetingUserList
        for (user in usersIdList) {
            if (zoomSDKInstance.inMeetingService.isHostUser(user)) {
                Log.d(TAG, "showActiveVideoLayout: userFound")
                defaultVideoManager.addAttendeeVideoUnit(user, activeRenderInfo)
                break
            }
        }
    }

    private fun showShareViewVideoLayout() {
        val userSharingId = zoomSDKInstance.inMeetingService.activeShareUserID()
        val shareRenderInfo = MobileRTCRenderInfo(0, 0, 100, 100)

        defaultVideoManager.removeAllVideoUnits()
        defaultVideoManager.addShareVideoUnit(userSharingId, shareRenderInfo)
    }

    private fun showConnectingView() {

    }

    private fun isOtherUserSharing(): Boolean {
        return zoomSDKInstance.inMeetingService.inMeetingShareController.isOtherSharing
    }

    private fun isMySelfWebinarAttendee(): Boolean {
        val myUserInfo: InMeetingUserInfo = zoomSDKInstance.inMeetingService.myUserInfo
        return if (zoomSDKInstance.inMeetingService.isWebinarMeeting) {
            myUserInfo.inMeetingUserRole == InMeetingUserInfo.InMeetingUserRole.USERROLE_ATTENDEE
        } else false
    }

    companion object {
        const val TAG = "MainFragment"
    }
}