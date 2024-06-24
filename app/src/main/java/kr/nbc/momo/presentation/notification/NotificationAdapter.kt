package kr.nbc.momo.presentation.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.RvItemSubscriptionBinding
import kr.nbc.momo.presentation.group.model.GroupModel
class NotificationAdapter(private var items: MutableList<Pair<GroupModel, String>>): RecyclerView.Adapter<NotificationAdapter.NotificationAdapterHolder>() {
    interface Confirm{
        fun confirm(groupId: String, userId: String, limitPerson: Int, userListSize: Int)
    }
    var confirm: Confirm? = null

    interface Reject{
        fun reject(groupId: String, userId: String)
    }
    var reject: Reject? = null

    interface UserClick{
        fun userClick(userId: String)
    }
    var userClick: UserClick? = null

    class NotificationAdapterHolder(binding: RvItemSubscriptionBinding) : RecyclerView.ViewHolder(binding.root) {
        val groupId = binding.tvGroupId
        val userId = binding.tvUserId
        val btnConfirm = binding.btnConfirm
        val btnReject = binding.btnReject
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapterHolder {
        val binding =
            RvItemSubscriptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NotificationAdapterHolder, position: Int) {
        holder.groupId.text = items[position].first.groupName
        holder.userId.text = items[position].second
        holder.btnConfirm.setOnClickListener {
            confirm?.confirm(items[position].first.groupId, items[position].second, items[position].first.limitPerson.toInt(), items[position].first.userList.size)
        }

        holder.btnReject.setOnClickListener {
            reject?.reject(items[position].first.groupId, items[position].second)
        }

        holder.userId.setOnClickListener {
            userClick?.userClick(items[position].second)
        }

    }

}
