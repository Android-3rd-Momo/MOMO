package kr.nbc.momo.presentation.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemSubscriptionBinding
import kr.nbc.momo.presentation.group.model.GroupModel

class SubscriptionGroupAdapter(private var items: MutableList<Pair<GroupModel, String>>): RecyclerView.Adapter<SubscriptionGroupAdapter.Holder>() {
    interface ItemClick{
        fun itemClick(groupId: String, userId: String)
    }
    var itemClick: ItemClick? = null

    class Holder(binding: RvItemSubscriptionBinding) : RecyclerView.ViewHolder(binding.root) {
        val groupId = binding.tvGroupId
        val userId = binding.tvUserId
        val btn = binding.btnConfirm
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            RvItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.groupId.text = items[position].first.groupName
        holder.userId.text = items[position].second
        holder.btn.setOnClickListener {
            itemClick?.itemClick(items[position].first.groupId, items[position].second)
        }

    }

}