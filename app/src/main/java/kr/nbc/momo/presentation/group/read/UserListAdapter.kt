package kr.nbc.momo.presentation.group.read

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.R
import kr.nbc.momo.databinding.GridviewItemBinding

class UserListAdapter(private val userList: List<String>, private val leaderId: String, private val context: Context) :
    RecyclerView.Adapter<UserListAdapter.UserListAdapterHolder>() {
    interface ItemClick {
        fun itemClick(userId: String)
    }

    var itemClick: ItemClick? = null

    class UserListAdapterHolder(binding: GridviewItemBinding ) : RecyclerView.ViewHolder(binding.root) {
        val userId = binding.tvUserId
        val root = binding.clAdapterItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapterHolder {
        val binding = GridviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserListAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserListAdapterHolder, position: Int) {
        if (leaderId == userList[position]) {
            holder.userId.setTextColor(ContextCompat.getColor(context, R.color.base_chip_text))
            holder.root.setBackgroundResource(R.drawable.bg_layout_corner_stroke_blue)
        }

        holder.userId.text = userList[position]
        holder.itemView.setOnClickListener {
            itemClick?.itemClick(userList[position])
        }
    }

}