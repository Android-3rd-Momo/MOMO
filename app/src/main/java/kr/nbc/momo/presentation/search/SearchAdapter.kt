package kr.nbc.momo.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemHomeVerticalBinding
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.util.setGroupImageByUrlOrDefault

class SearchAdapter(
    private val onClick: (GroupModel) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    var itemList = listOf<GroupModel>()

    class SearchViewHolder(
        private val onClick: (GroupModel) -> Unit,
        private val binding: RvItemHomeVerticalBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(groupModel: GroupModel) {
            with(binding) {
                ivGroupImage.setGroupImageByUrlOrDefault(groupModel.groupThumbnail)
                tvName.text = groupModel.groupName
                tvDescription.text = groupModel.groupDescription
                tvCategory.text = groupModel.category.developmentOccupations.joinToString()
            }
            itemView.setOnClickListener {
                onClick(groupModel)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = RvItemHomeVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(onClick, binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(itemList[position])
    }
}