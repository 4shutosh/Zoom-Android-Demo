package com.poc.zoom.inmeeting.question

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import us.zoom.sdk.ZoomSDK
import javax.inject.Inject

@HiltViewModel
class QuestionDialogViewModel @Inject constructor(
    private val zoomSDK: ZoomSDK
) : ViewModel() {

    val dismissTrigger = MutableLiveData<String>()

    fun sendUserAnswer(documentId: String, userName: String, response: String) {
        val meetingNumber = zoomSDK.inMeetingService.currentMeetingNumber

        val hashmap = hashMapOf(
            "response" to response
        )

        Firebase.firestore.collection("Responses").document(meetingNumber.toString())
            .collection(documentId).document(userName).set(hashmap).addOnSuccessListener {
                dismissTrigger.postValue(response)
            }
    }
}