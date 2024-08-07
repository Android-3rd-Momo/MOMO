package kr.nbc.momo.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemHomeVerticalBinding
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.util.setGroupImageByUrlOrDefault

class LatestGroupListAdapter(private var items: List<GroupModel>): RecyclerView.Adapter<LatestGroupListAdapter.LatestGroupListAdapterHolder>() {
    interface ItemClick{
        fun itemClick(position: Int)
    }
    var itemClick: ItemClick? = null

    class LatestGroupListAdapterHolder(binding: RvItemHomeVerticalBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivGroupImage
        val name = binding.tvName
        val description = binding.tvDescription
        val category = binding.tvCategory
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestGroupListAdapterHolder {
        val binding = RvItemHomeVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LatestGroupListAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LatestGroupListAdapterHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.itemClick(position)
        }
        holder.image.setGroupImageByUrlOrDefault(items[position].groupThumbnail)
        holder.name.text = items[position].groupName
        holder.description.text = items[position].groupOneLineDescription
        holder.category.text = items[position].category.classification
    }

}