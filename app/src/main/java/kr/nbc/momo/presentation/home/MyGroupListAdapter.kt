package kr.nbc.momo.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemHomeHorizontalBinding
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.util.setGroupImageByUrlOrDefault

class MyGroupListAdapter(private var items: List<GroupModel>): RecyclerView.Adapter<MyGroupListAdapter.MyGroupListAdapterHolder>() {
    interface ItemClick{
        fun itemClick(position: Int)
    }
    var itemClick: ItemClick? = null

    class MyGroupListAdapterHolder(binding: RvItemHomeHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivGroupImage
        val name = binding.tvGroupName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGroupListAdapterHolder {
        val binding = RvItemHomeHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyGroupListAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyGroupListAdapterHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.itemClick(position)
        }
        holder.image.clipToOutline = true
        holder.image.setGroupImageByUrlOrDefault(items[position].groupThumbnail)
        holder.name.text = items[position].groupName
    }

}