package kr.nbc.momo.presentation.chattingroom

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemElseBinding
import kr.nbc.momo.databinding.RvItemUserBinding
import kr.nbc.momo.presentation.chattingroom.model.ChatModel
import kr.nbc.momo.presentation.chattingroom.model.GroupChatModel
import kr.nbc.momo.presentation.chattingroom.util.setDateTimeFormatToMMDD
import kr.nbc.momo.presentation.chattingroom.util.setDateTimeFormatToYYYYmmDD
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class RVAdapter(private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList = GroupChatModel()

    class ItemElseViewHolder(
        private val binding: RvItemElseBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            chatModel: ChatModel,
            isDateChanged: Boolean,
            isMinuteChanged: Boolean,
            isUserChanged: Boolean
        ) {
            with(binding) {
                tvChat.text = chatModel.text
                tvTime.text = setDateTimeFormatToMMDD(chatModel.dateTime)
                tvDivider.text = setDateTimeFormatToYYYYmmDD(chatModel.dateTime)
                tvUserName.text = chatModel.userName
                //유저 바뀌면 이름 보여주기
                if (isUserChanged) {
                    tvUserName.visibility = View.VISIBLE
                    tvTime.visibility = View.VISIBLE
                } else {
                    tvUserName.visibility = View.GONE
                }

                if (isMinuteChanged) {
                    tvTime.visibility = View.VISIBLE
                    tvUserName.visibility = View.VISIBLE
                } else {
                    tvTime.visibility = View.GONE
                }

                //날 바뀌면 divider 보여주기
                if (isDateChanged) {
                    tvDivider.visibility = View.VISIBLE
                    tvUserName.visibility = View.VISIBLE
                } else {
                    tvDivider.visibility = View.GONE
                }
            }
        }
    }

    class ItemUserHolder(
        private val binding: RvItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            chatModel: ChatModel,
            isDateChanged: Boolean,
            isMinuteChanged: Boolean,
            isUserChanged: Boolean
        ) {
            with(binding) {
                Log.d("tvTimeText" , "${chatModel.dateTime}")
                tvChat.text = chatModel.text
                tvTime.text = setDateTimeFormatToMMDD(chatModel.dateTime)
                Log.d("tvTimeText" , "${tvTime.text} + ${itemId}")
                tvDivider.text = setDateTimeFormatToYYYYmmDD(chatModel.dateTime)
                //유저 바뀌면 이름 보여주기
                tvUserName.visibility = View.GONE
                if (isUserChanged) {
                    tvTime.visibility = View.VISIBLE
                }

                if (isMinuteChanged) {
                    tvTime.visibility = View.VISIBLE
                } else {
                    tvTime.visibility = View.GONE
                }

                //날 바뀌면 divider 보여주기
                if (isDateChanged) {
                    tvDivider.visibility = View.VISIBLE
                } else {
                    tvDivider.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemList.chatList[position].userId == currentUserId) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding =
                    RvItemElseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemElseViewHolder(binding)
            }

            else -> {
                val binding =
                    RvItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemUserHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.chatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (itemList.chatList[position].userId) {
            currentUserId -> (holder as ItemUserHolder).bind(
                itemList.chatList[position],
                isDateChanged(position),
                isMinuteChanged(position),
                isUserIdChanged(position)
            )

            else -> (holder as ItemElseViewHolder).bind(
                itemList.chatList[position],
                isDateChanged(position),
                isMinuteChanged(position),
                isUserIdChanged(position)
            )
        }
    }

    private fun isDateChanged(position: Int): Boolean {
        if (position == 0) return true
        val prevDate = ZonedDateTime.parse(itemList.chatList[position - 1].dateTime).dayOfYear
        val currentDate = ZonedDateTime.parse(itemList.chatList[position].dateTime).dayOfYear
        return prevDate != currentDate
    }

    private fun isMinuteChanged(position: Int): Boolean {
        if (position == itemCount - 1) return true
        val nextMin = ZonedDateTime.parse(itemList.chatList[position + 1].dateTime)
            .truncatedTo(ChronoUnit.MINUTES)
        val currentMin = ZonedDateTime.parse(itemList.chatList[position].dateTime)
            .truncatedTo(ChronoUnit.MINUTES)

        return nextMin != currentMin
    }

    private fun isUserIdChanged(position: Int): Boolean {
        if (position == 0) return true
        val prevUserId = itemList.chatList[position - 1].userId
        val currentUserId = itemList.chatList[position].userId

        return prevUserId != currentUserId
    }
}
