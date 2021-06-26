package com.poc.zoom.utils

import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

typealias NavigationCommand = (NavController) -> Unit

@ActivityRetainedScoped
class ActivityNavigationDispatchers @Inject constructor() {

    private val navigationEmitter = MutableLiveData<Event<NavigationCommand>>()
    val navigationCommand = navigationEmitter.toLiveData()

    fun emit(navigationCommand: NavigationCommand) {
        navigationEmitter.postValue(Event(navigationCommand))
    }

    fun navigate(direction : () -> NavDirections) {
        navigationEmitter.postValue(direction.invoke().toEvent())
    }
}