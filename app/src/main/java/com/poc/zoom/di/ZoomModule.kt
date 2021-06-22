package com.poc.zoom.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import us.zoom.sdk.InMeetingService
import us.zoom.sdk.MeetingService
import us.zoom.sdk.ZoomSDK

@Module
@InstallIn(ActivityRetainedComponent::class)
object ZoomModule {

    @Provides
    fun providesZoomSDKInstance(): ZoomSDK = ZoomSDK.getInstance()

    @Provides
    fun providesZoomSDKMeetingService(zoomSDK: ZoomSDK): MeetingService =
        zoomSDK.meetingService

    @Provides
    fun providesZoomSDKInMeetingService(zoomSDK: ZoomSDK): InMeetingService =
        zoomSDK.inMeetingService
}