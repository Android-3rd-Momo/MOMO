package kr.nbc.momo.presentation.chattingroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemBinding
import kr.nbc.momo.presentation.chattingroom.model.ChatModel
import kr.nbc.momo.presentation.chattingroom.model.GroupChatModel

class RVAdapter():RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
    var itemList = GroupChatModel()
    class ItemViewHolder(
        private val binding: RvItemBinding,
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(chatModel: ChatModel){
            with(binding){
                tv1.text = chatModel.text
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.chatList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList.chatList[position])
    }
}
