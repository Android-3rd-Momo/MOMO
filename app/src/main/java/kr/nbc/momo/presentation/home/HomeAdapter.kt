package kr.nbc.momo.presentation.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.google.firebase.storage.StorageReference
import kr.nbc.momo.databinding.RvHomeItemBinding
import kr.nbc.momo.presentation.group.model.GroupModel

class HomeAdapter(private var items: List<GroupModel>): RecyclerView.Adapter<HomeAdapter.Holder>() {
    interface ItemClick{
        fun itemClick(position: Int)
    }
    var itemClick: ItemClick? = null

    class Holder(binding: RvHomeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivGroupImage
        val name = binding.name
        val description = binding.description
        val category = binding.category
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RvHomeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClick?.itemClick(position)
        }

        holder.image.load(items[position].downloadUri)
        holder.name.text =items[position].groupName
        holder.description.text =items[position].groupDescription
        holder.category.text =items[position].categoryList.joinToString()
    }

}