package kr.nbc.momo.presentation.chatting.chattinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import kr.nbc.momo.databinding.RvItemChattingListBinding
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel

class ChattingListRecyclerViewAdapter(
    private val onClick: (String) -> Unit
): RecyclerView.Adapter<ChattingListRecyclerViewAdapter.ChattingListItemHolder>() {
    var itemList = listOf<ChattingListModel>()

    class ChattingListItemHolder(
        private val onClick: (String) -> Unit,
        private val binding: RvItemChattingListBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(chattingListModel: ChattingListModel){
            with(binding){
                ivGroupImage.load(chattingListModel.groupThumbnailUrl)
                tvGroupName.text = chattingListModel.groupName
                tvLatestChatText.text = chattingListModel.latestChatMessage
            }
            chattingListModel.apply { onClick(groupId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingListItemHolder {
        val binding = RvItemChattingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChattingListItemHolder(onClick, binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ChattingListItemHolder, position: Int) {
        holder.bind(itemList[position])
    }
}