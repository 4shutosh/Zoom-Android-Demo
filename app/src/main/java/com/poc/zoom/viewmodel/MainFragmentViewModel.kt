package com.poc.zoom.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.poc.zoom.utils.toLiveData
import com.poc.zoom.viewState.QuestionViewState
import com.poc.zoom.zoomhelper.ZoomMeetingUiSettingHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import us.zoom.sdk.*
import java.util.*
import javax.inject.Inject


/*register listeners : Primary should be video and audio, password requirenment listener
 todo : start video, make the DI more efficient : remove unwanted
* */

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val zoomSDKInstance: ZoomSDK,
    private val meetingService: MeetingService,
    private val inMeetingService: InMeetingService
) : ViewModel() {

    private var _meetingLayoutType = MutableLiveData<Int>()
    var meetingLayoutType = _meetingLayoutType.toLiveData()

    private var _buttonV = MutableLiveData<Boolean>()
    var buttonV = _buttonV.toLiveData()

    private var _questionData = MutableLiveData<QuestionViewState>()
    var questionViewState = _questionData.toLiveData()

    init {
        initiateZoom()
    }

    private fun initiateZoom() {
        zoomSDKInstance.meetingSettingsHelper.isCustomizedMeetingUIEnabled = true
    }

    // recheck context as a parameter
    fun joinMeeting(number: String, name: String, context: Context) {
        // joinMeetingHere
        val params = JoinMeetingParams()
        params.meetingNo = number
        params.displayName = name

        zoomSDKInstance.meetingSettingsHelper.setAutoConnectVoIPWhenJoinMeeting(true)
        zoomSDKInstance.meetingSettingsHelper.enable720p(true)
        val options = ZoomMeetingUiSettingHelper.getJoinMeetingOptions()
        meetingService.joinMeetingWithParams(context, params, options)
    }

    fun getNewVideoMeetingLayout(): Int {
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
        return newLayoutType
    }

    private fun isMySelfWebinarAttendee(): Boolean {
        val myUserInfo: InMeetingUserInfo = inMeetingService.myUserInfo
        return if (inMeetingService.isWebinarMeeting) {
            myUserInfo.inMeetingUserRole == InMeetingUserInfo.InMeetingUserRole.USERROLE_ATTENDEE
        } else false
    }

    fun startQuestionFeature(meetingNumber: Long) {

        val database = Firebase.firestore

        // make a child of meetingNumber in database
        database.collection("Questions")
            .document(meetingNumber.toString()).collection("questions")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                value?.let {
                    if (!it.isEmpty) {
                        for (question in it) {
                            if (question.get("question") != null) {
                                if (question.getBoolean("posted") == false) {
                                    val options =
                                        question.get("options") as ArrayList<*>
                                    val questionViewState =
                                        QuestionViewState(
                                            question.id,
                                            question.getString("question").orEmpty(),
                                            options[0].toString(),
                                            options[1].toString(),
                                            options[2].toString(),
                                            options[3].toString(),
                                        )
                                    _questionData.postValue(questionViewState)
                                    _buttonV.postValue(true)
                                    Log.d(
                                        TAG,
                                        "startQuestionFeature: question posted : $questionViewState"
                                    )
                                    break
                                } else {
                                    _buttonV.postValue(false)
                                }
                            }
                        }
                    }
                }
            }
    }

    companion object {

        private const val TAG = "MainFragmentViewModel"

        const val WEB_DOMAIN = "zoom.us"

        // need to implement
        var currentLayoutType = -1
        const val LAYOUT_TYPE_PREVIEW = 0
        const val LAYOUT_TYPE_WAITHOST = 1
        const val LAYOUT_TYPE_IN_WAIT_ROOM = 2
        const val LAYOUT_TYPE_ONLY_MYSELF = 3
        const val LAYOUT_TYPE_ONETOONE = 4
        const val LAYOUT_TYPE_LIST_VIDEO = 5
        const val LAYOUT_TYPE_OTHER_SHARE = 6
        const val LAYOUT_TYPE_SHARING_VIEW = 7
        const val LAYOUT_TYPE_WEBINAR_ATTENDEE = 8
    }
}