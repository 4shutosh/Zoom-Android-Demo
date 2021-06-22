package com.poc.zoom.utils

open class BaseCallback<T : BaseEvent?> {
    var callbacks: MutableList<T> = ArrayList()
    private fun init() {}
    fun addListener(event: T) {
        if (!callbacks.contains(event)) {
            callbacks.add(event)
        }
    }

    fun removeListener(event: T) {
        callbacks.remove(event)
    }
}

interface BaseEvent