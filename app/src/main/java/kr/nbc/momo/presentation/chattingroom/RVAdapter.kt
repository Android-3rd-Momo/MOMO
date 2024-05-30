package kr.nbc.momo.presentation.chattingroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemElseBinding
import kr.nbc.momo.databinding.RvItemUserBinding
import kr.nbc.momo.presentation.chattingroom.model.ChatModel
import kr.nbc.momo.presentation.chattingroom.model.GroupChatModel
import kr.nbc.momo.presentation.chattingroom.util.setDateTimeFormatToKorea
import java.text.SimpleDateFormat

class RVAdapter(private val currentUserId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var itemList = GroupChatModel()

    class ItemElseViewHolder(
        private val binding: RvItemElseBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatModel: ChatModel) {
            with(binding) {
                tv1.text = chatModel.text + setDateTimeFormatToKorea(chatModel.dateTime)

            }
        }
    }

    class ItemUserHolder(
        private val binding: RvItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatModel: ChatModel) {
            with(binding) {
                tv1.text = chatModel.text + setDateTimeFormatToKorea(chatModel.dateTime)
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
            currentUserId -> (holder as ItemUserHolder).bind(itemList.chatList[position])
            else -> (holder as ItemElseViewHolder).bind(itemList.chatList[position])
        }
    }
}
