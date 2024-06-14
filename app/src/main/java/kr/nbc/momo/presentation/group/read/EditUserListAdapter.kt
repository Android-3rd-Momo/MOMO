package kr.nbc.momo.presentation.group.read

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.GridviewItemBinding

class EditUserListAdapter(private val userList: List<String>) :
    RecyclerView.Adapter<EditUserListAdapter.Holder>() {
    interface LongClick {
        fun longClick(userId: String)
    }

    var longClick: LongClick? = null

    class Holder(binding: GridviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val userId = binding.tvUserId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = GridviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.userId.text = userList[position]

        holder.itemView.setOnLongClickListener {
            longClick?.longClick(userList[position])
            true
        }

    }

}