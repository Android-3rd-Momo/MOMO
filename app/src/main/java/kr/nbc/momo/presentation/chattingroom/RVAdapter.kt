package kr.nbc.momo.presentation.chattingroom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemElseBinding
import kr.nbc.momo.databinding.RvItemUserBinding
import kr.nbc.momo.presentation.chattingroom.model.ChatModel
import kr.nbc.momo.presentation.chattingroom.model.GroupChatModel
import kr.nbc.momo.presentation.chattingroom.util.setDateTimeFormatToYYYYmmDD
import java.time.ZonedDateTime

class RVAdapter(private val currentUserId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList = GroupChatModel()

    class ItemElseViewHolder(
        private val binding: RvItemElseBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatModel: ChatModel, isDateChanged: Boolean) {
            with(binding) {
                tv1.text = chatModel.text
                if(isDateChanged){
                    tvDivider.visibility = View.VISIBLE
                    tvDivider.text = setDateTimeFormatToYYYYmmDD(chatModel.dateTime)
                }else{
                    tvDivider.visibility = View.GONE
                }
            }
        }
    }

    class ItemUserHolder(
        private val binding: RvItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatModel: ChatModel, isDateChanged: Boolean) {
            with(binding) {
                tv1.text = chatModel.text
                if(isDateChanged){
                    tvDivider.visibility = View.VISIBLE
                    tvDivider.text = setDateTimeFormatToYYYYmmDD(chatModel.dateTime)
                }else{
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
        when(itemList.chatList[position].userId){
            currentUserId -> (holder as ItemUserHolder).bind(itemList.chatList[position], isDateChanged(position))
            else -> (holder as ItemElseViewHolder).bind(itemList.chatList[position], isDateChanged(position))
        }
    }

    private fun isDateChanged(position: Int): Boolean{
        if (position == 0) return true
        val prevDate = ZonedDateTime.parse(itemList.chatList[position - 1].dateTime).dayOfYear
        val currentDate = ZonedDateTime.parse(itemList.chatList[position].dateTime).dayOfYear
        return prevDate != currentDate
    }
}
