package kr.nbc.momo.presentation.chattingroom

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

class RVAdapter(private val currentUserId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList = GroupChatModel()

    class ItemElseViewHolder(
        private val binding: RvItemElseBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatModel: ChatModel, isDateChanged: Boolean, isMinuteChanged: Boolean) {
            with(binding) {
                tvChat.text = chatModel.text
                tvTime.text = setDateTimeFormatToMMDD(chatModel.dateTime)
                if(isDateChanged){
                    tvDivider.visibility = View.VISIBLE
                    tvDivider.text = setDateTimeFormatToYYYYmmDD(chatModel.dateTime)
                }else{
                    tvDivider.visibility = View.GONE
                }
                if (isMinuteChanged){
                    tvUserName.visibility = View.VISIBLE
                    tvUserName.text = "지금은 없는 유저이름 추가하기"
                }else{
                    tvUserName.visibility = View.GONE
                }
            }
        }
    }

    class ItemUserHolder(
        private val binding: RvItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatModel: ChatModel, isDateChanged: Boolean, isMinuteChanged: Boolean) {
            with(binding) {
                tvChat.text = chatModel.text
                tvTime.text = setDateTimeFormatToMMDD(chatModel.dateTime)
                if(isDateChanged){
                    tvDivider.visibility = View.VISIBLE
                    tvDivider.text = setDateTimeFormatToYYYYmmDD(chatModel.dateTime)
                }else{
                    tvDivider.visibility = View.GONE
                }
                if (isMinuteChanged){
                    tvUserName.visibility = View.VISIBLE
                    tvUserName.text = "지금은 없는 유저이름 추가하기"
                }else{
                    tvUserName.visibility = View.GONE
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
        when(itemList.chatList[position].userId){
            currentUserId -> (holder as ItemUserHolder).bind(itemList.chatList[position], isDateChanged(position), isMinuteChanged(position))
            else -> (holder as ItemElseViewHolder).bind(itemList.chatList[position], isDateChanged(position), isMinuteChanged(position))
        }
    }

    private fun isDateChanged(position: Int): Boolean{
        if (position == 0) return true
        val prevDate = ZonedDateTime.parse(itemList.chatList[position - 1].dateTime).dayOfYear
        val currentDate = ZonedDateTime.parse(itemList.chatList[position].dateTime).dayOfYear
        return prevDate != currentDate
    }

    private fun isMinuteChanged(position: Int): Boolean{
        if (position == 0) return true
        val prevMin = ZonedDateTime.parse(itemList.chatList[position - 1].dateTime).minute
        val currentMin = ZonedDateTime.parse(itemList.chatList[position].dateTime).minute

        return prevMin != currentMin
    }

    private fun isUserIdChanged(position: Int): Boolean{
        val prevUserId = itemList.chatList[position - 1].userId
        val currentUserId = itemList.chatList[position].userId

        return prevUserId != currentUserId
    }
}
