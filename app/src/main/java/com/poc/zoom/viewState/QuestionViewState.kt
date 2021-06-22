package com.poc.zoom.viewState

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuestionViewState(
    val id: String,
    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String
) : Parcelable