package kr.nbc.momo.presentation.chatting.chattingroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.R
import kr.nbc.momo.databinding.RvItemElseBinding
import kr.nbc.momo.databinding.RvItemErrorBinding
import kr.nbc.momo.databinding.RvItemUserBinding
import kr.nbc.momo.presentation.chatting.chattingroom.model.ChatModel
import kr.nbc.momo.presentation.chatting.chattingroom.model.GroupChatModel
import kr.nbc.momo.presentation.chatting.chattingroom.model.GroupUserModel
import kr.nbc.momo.presentation.chatting.chattingroom.multi.ChattingEnumClass
import kr.nbc.momo.util.setDateTimeFormatToMMDD
import kr.nbc.momo.util.setDateTimeFormatToYYYYmmDD
import kr.nbc.momo.util.setThumbnailByUrlOrDefault
import kr.nbc.momo.util.setVisibleToGone
import kr.nbc.momo.util.setVisibleToInvisible
import kr.nbc.momo.util.setVisibleToVisible
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ChattingRecyclerViewAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList = GroupChatModel()
    var currentUserId = ""
    var currentUserName = ""
    var currentUrl = ""

    override fun getItemViewType(position: Int): Int {
        return when (itemList.chatList[position].userId == currentUserId) {
            true -> ChattingEnumClass.USER_VIEW_TYPE.type
            else -> ChattingEnumClass.ELSE_VIEW_TYPE.type
        }
    }

    interface ItemClick {
        fun itemClick(userId: String)
    }

    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ChattingEnumClass.USER_VIEW_TYPE.type -> {
                val binding =
                    RvItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemUserHolder(binding)
            }

            ChattingEnumClass.ELSE_VIEW_TYPE.type -> {
                val binding =
                    RvItemElseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemElseViewHolder(binding, itemClick)
            }

            else -> {
                val binding =
                    RvItemErrorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemErrorHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.chatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ChattingEnumClass.USER_VIEW_TYPE.type -> {
                (holder as ItemUserHolder)
                    .bind(
                        itemList.chatList[position],
                        isDateChanged(position),
                        isMinuteChanged(position),
                        isUserIdChanged(position),
                        isNextUserChanged(position)
                    )
            }


            ChattingEnumClass.ELSE_VIEW_TYPE.type -> {
                (holder as ItemElseViewHolder).bind(
                    itemList.userList.firstOrNull { it.userId == itemList.chatList[position].userId }
                        ?: GroupUserModel(),
                    itemList.chatList[position],
                    isDateChanged(position),
                    isMinuteChanged(position),
                    isUserIdChanged(position),
                    isNextUserChanged(position),
                    isPrevMinuteChanged(position)
                )
            }

            else -> (holder as ItemErrorHolder).bind()
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

    private fun isNextUserChanged(position: Int): Boolean {
        if (position == itemCount - 1) return true
        val nextUser = itemList.chatList[position + 1].userId
        val currentUser = itemList.chatList[position].userId
        return currentUser != nextUser
    }

    private fun isPrevMinuteChanged(position: Int): Boolean {
        if (position == 0) return false
        val prevMin = ZonedDateTime.parse(itemList.chatList[position - 1].dateTime)
            .truncatedTo(ChronoUnit.MINUTES)
        val currentMin = ZonedDateTime.parse(itemList.chatList[position].dateTime)
            .truncatedTo(ChronoUnit.MINUTES)

        return prevMin != currentMin
    }


    class ItemElseViewHolder(
        private val binding: RvItemElseBinding,
        private val itemClick: ItemClick?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            userModel: GroupUserModel,
            chatModel: ChatModel,
            isDateChanged: Boolean,
            isMinuteChanged: Boolean,
            isUserChanged: Boolean,
            isNextUserChanged: Boolean,
            isPrevMinuteChanged: Boolean
        ) {
            with(binding) {
                tvChat.text = chatModel.text
                tvTime.text = chatModel.dateTime.setDateTimeFormatToMMDD()
                tvDivider.text = chatModel.dateTime.setDateTimeFormatToYYYYmmDD()
                tvUserName.text = chatModel.userName
                ivProfile.setThumbnailByUrlOrDefault(userModel.userProfileUrl)
                //유저 바뀌면 이름 보여주기
                if (isUserChanged) {
                    cardView.setVisibleToVisible()
                    tvUserName.setVisibleToVisible()
                    tvTime.setVisibleToVisible()
                } else {
                    cardView.setVisibleToInvisible()
                    tvUserName.setVisibleToGone()
                    tvTime.setVisibleToGone()
                }
                if (isMinuteChanged) {
                    tvTime.setVisibleToVisible()
                } else {
                    tvTime.setVisibleToGone()
                }

                if (isNextUserChanged) {
                    tvTime.setVisibleToVisible()
                }
                if (isPrevMinuteChanged) {
                    cardView.setVisibleToVisible()
                    tvUserName.setVisibleToVisible()
                }

                if (isDateChanged) {
                    cardView.setVisibleToVisible()
                    tvUserName.setVisibleToVisible()
                    tvTime.setVisibleToVisible()
                    tvDivider.setVisibleToVisible()
                    if (!isMinuteChanged) tvTime.setVisibleToGone()

                } else {
                    tvDivider.setVisibleToGone()
                }

                ivProfile.setOnClickListener {
                    itemClick?.itemClick(userModel.userId)
                }
            }
        }
    }

    class ItemUserHolder(
        private val binding: RvItemUserBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            chatModel: ChatModel,
            isDateChanged: Boolean,
            isMinuteChanged: Boolean,
            isUserChanged: Boolean,
            isNextUserChanged: Boolean
        ) {
            with(binding) {
                tvChat.text = chatModel.text
                tvTime.text = chatModel.dateTime.setDateTimeFormatToMMDD()
                tvDivider.text = chatModel.dateTime.setDateTimeFormatToYYYYmmDD()

                tvUserName.setVisibleToGone()
                if (isUserChanged) {
                    tvTime.setVisibleToVisible()
                }

                if (isMinuteChanged) {
                    tvTime.setVisibleToVisible()
                } else {
                    if (isNextUserChanged) tvTime.setVisibleToVisible()
                    else tvTime.setVisibleToGone()
                }

                //날 바뀌면 divider 보여주기
                if (isDateChanged) {
                    tvDivider.setVisibleToVisible()
                } else {
                    tvDivider.setVisibleToGone()
                }
            }
        }
    }

    class ItemErrorHolder(
        private val binding: RvItemErrorBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            with(binding) {
                tvChat.setText(R.string.unknown_view_type_error)
            }
        }
    }
}
