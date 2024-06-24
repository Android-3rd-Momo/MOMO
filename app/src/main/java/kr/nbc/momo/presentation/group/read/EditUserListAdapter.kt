package kr.nbc.momo.presentation.group.read

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.R
import kr.nbc.momo.databinding.GridviewItemBinding
import kr.nbc.momo.util.setVisibleToVisible

class EditUserListAdapter(private val userList: List<String>, private val leaderId: String, private val context: Context) :
    RecyclerView.Adapter<EditUserListAdapter.EditUserListAdapterHolder>() {
    interface LongClick {
        fun longClick(userId: String)
    }

    var longClick: LongClick? = null

    interface OnClick {
        fun onClick(userId: String)
    }

    var onClick: OnClick? = null

    class EditUserListAdapterHolder(binding: GridviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val userId = binding.tvUserId
        val btnDelete = binding.ivDelete
        val root = binding.clAdapterItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditUserListAdapterHolder {
        val binding = GridviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EditUserListAdapterHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: EditUserListAdapterHolder, position: Int) {
        if (leaderId == userList[position]) {
            holder.userId.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.root.setBackgroundResource(R.drawable.bg_layout_corner_stroke_blue)
        }

        holder.userId.text = userList[position]

        holder.itemView.setOnLongClickListener {
            longClick?.longClick(userList[position])
            true
        }

        if (leaderId != userList[position]) {
            holder.btnDelete.setVisibleToVisible()
        }
        holder.btnDelete.setOnClickListener {
            onClick?.onClick(userList[position])
        }

    }

}