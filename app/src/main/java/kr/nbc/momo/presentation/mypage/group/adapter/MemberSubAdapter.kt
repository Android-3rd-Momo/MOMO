package kr.nbc.momo.presentation.mypage.group.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemHomeVerticalBinding
import kr.nbc.momo.presentation.group.model.GroupModel
import kr.nbc.momo.util.setGroupImageByUrlOrDefault
import kr.nbc.momo.util.setVisibleToVisible

class MemberSubAdapter(private var items: List<GroupModel>): RecyclerView.Adapter<MemberSubAdapter.MemberSubAdapterHolder>() {
    interface ItemClick{
        fun itemClick(position: Int)
    }
    var itemClick: ItemClick? = null

    interface ExitClick{
        fun exitClick(groupId: String)
    }
    var exitClick: ExitClick? = null

    class MemberSubAdapterHolder(binding: RvItemHomeVerticalBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivGroupImage
        val name = binding.tvName
        val description = binding.tvDescription
        val category = binding.tvCategory
        val exit = binding.tvExit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberSubAdapterHolder {
        val binding = RvItemHomeVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberSubAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MemberSubAdapterHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.itemClick(position)
        }
        holder.exit.setVisibleToVisible()
        holder.exit.setOnClickListener {
            exitClick?.exitClick(items[position].groupId)
        }
        holder.image.setGroupImageByUrlOrDefault(items[position].groupThumbnail)
        holder.name.text = items[position].groupName
        holder.description.text = items[position].groupOneLineDescription
        holder.category.text = items[position].category.classification
    }

}