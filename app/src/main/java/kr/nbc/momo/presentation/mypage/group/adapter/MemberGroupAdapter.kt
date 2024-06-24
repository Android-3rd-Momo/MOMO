package kr.nbc.momo.presentation.mypage.group.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemHomeHorizontalBinding
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.util.setThumbnailByUrlOrDefault

class MemberGroupAdapter(private var items: List<GroupModel>): RecyclerView.Adapter<MemberGroupAdapter.Holder>() {
    interface ItemClick{
        fun itemClick(position: Int)
    }
    var itemClick: ItemClick? = null

    class Holder(binding: RvItemHomeHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivGroupImage
        val name = binding.tvGroupName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RvItemHomeHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.itemClick(position)
        }
        holder.image.clipToOutline = true
        holder.image.setThumbnailByUrlOrDefault(items[position].groupThumbnail)
        holder.name.text = items[position].groupName
    }

}