package kr.nbc.momo.presentation.chatting.chattinglist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemChattingListBinding
import kr.nbc.momo.presentation.chatting.chattinglist.model.ChattingListModel
import kr.nbc.momo.util.setThumbnailByUrlOrDefault
import kr.nbc.momo.util.setVisibleToInvisible
import kr.nbc.momo.util.setVisibleToVisible

class ChattingListRecyclerViewAdapter(
    private val onClick: (ChattingListModel) -> Unit
) : RecyclerView.Adapter<ChattingListRecyclerViewAdapter.ChattingListItemHolder>() {
    var itemList = listOf<ChattingListModel>()

    class ChattingListItemHolder(
        private val onClick: (ChattingListModel) -> Unit,
        private val binding: RvItemChattingListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chattingListModel: ChattingListModel) {
            with(binding) {
                ivGroupImage.setThumbnailByUrlOrDefault(chattingListModel.groupThumbnailUrl)
                tvGroupName.text = chattingListModel.groupName
                tvLatestChatText.text = chattingListModel.latestChatMessage
                tvLatestChatTime.text = chattingListModel.latestChatTimeGap
                tvSign.apply {
                    if (chattingListModel.latestChatIndexGap == 0) {
                        setVisibleToInvisible()
                    } else {
                        setVisibleToVisible()
                        text = chattingListModel.latestChatIndexGap.toString()
                    }
                }
            }
            itemView.setOnClickListener {
                onClick(chattingListModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChattingListItemHolder {
        val binding =
            RvItemChattingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChattingListItemHolder(onClick, binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ChattingListItemHolder, position: Int) {
        holder.bind(itemList[position])
    }
}