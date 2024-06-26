package kr.nbc.momo.presentation.mypage.group.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemHomeHorizontalBinding
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.util.setGroupImageByUrlOrDefault

class LeaderGroupAdapter(private var items: List<GroupModel>): RecyclerView.Adapter<LeaderGroupAdapter.LeaderGroupAdapterHolder>() {
    interface ItemClick{
        fun itemClick(position: Int)
    }
    var itemClick: ItemClick? = null

    class LeaderGroupAdapterHolder(binding: RvItemHomeHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivGroupImage
        val name = binding.tvGroupName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderGroupAdapterHolder {
        val binding = RvItemHomeHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaderGroupAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: LeaderGroupAdapterHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.itemClick(position)
        }
        holder.image.clipToOutline = true
        holder.image.setGroupImageByUrlOrDefault(items[position].groupThumbnail)
        holder.name.text = items[position].groupName
    }

}