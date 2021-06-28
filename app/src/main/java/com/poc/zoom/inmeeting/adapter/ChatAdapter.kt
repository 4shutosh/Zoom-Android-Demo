package com.poc.zoom.inmeeting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.poc.zoom.databinding.LayoutItemZoomChatMessageBinding
import com.poc.zoom.utils.executeAfter
import us.zoom.sdk.InMeetingChatMessage
import us.zoom.sdk.ZoomSDK

class ChatAdapter constructor() :
    ListAdapter<InMeetingChatMessage, InMeetingChatMessageHolder>(DiffCallBack()) {

    private val chatList = mutableListOf<InMeetingChatMessage>()

    private val zoomSDK = ZoomSDK.getInstance().inMeetingService

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InMeetingChatMessageHolder {
        val binding = LayoutItemZoomChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InMeetingChatMessageHolder(binding)
    }

    override fun onBindViewHolder(holder: InMeetingChatMessageHolder, position: Int) {
        val item = getItem(position)
        val name = getUserName(item.senderUserId)
        holder.itemBinding.executeAfter {
            this.viewState = item
            this.messageUser.text = name
        }
    }

    private fun getUserName(userId: Long): String {
        return zoomSDK.getUserInfoById(userId).userName.orEmpty()
    }

}

class InMeetingChatMessageHolder(val itemBinding: LayoutItemZoomChatMessageBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
}


class DiffCallBack : DiffUtil.ItemCallback<InMeetingChatMessage>() {
    override fun areItemsTheSame(
        oldItem: InMeetingChatMessage,
        newItem: InMeetingChatMessage
    ): Boolean {
        return oldItem.content == newItem.content
    }

    override fun areContentsTheSame(
        oldItem: InMeetingChatMessage,
        newItem: InMeetingChatMessage
    ): Boolean {
        return oldItem.content == newItem.content
    }

}