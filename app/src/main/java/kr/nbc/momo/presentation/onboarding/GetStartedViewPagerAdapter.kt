package kr.nbc.momo.presentation.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.R

class GetStartedViewPagerAdapter(
    private var title: List<String>,
    private var desc: List<String>,
    private var image: List<Int>
) : RecyclerView.Adapter<GetStartedViewPagerAdapter.Pager2ViewHolder>(){
    inner class Pager2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemTitle: TextView = itemView.findViewById(R.id.TextView_title)
        val itemDesc: TextView = itemView.findViewById(R.id.TextView_desc)
        val itemillust: ImageView = itemView.findViewById(R.id.ImageView_illust)

        init {
            itemillust.setOnClickListener { v: View ->
                val position = adapterPosition
                Toast.makeText(
                    itemView.context,
                    "You clicked on item = ${position}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GetStartedViewPagerAdapter.Pager2ViewHolder {
        return Pager2ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return title.size
    }

    override fun onBindViewHolder(holder: GetStartedViewPagerAdapter.Pager2ViewHolder, position: Int) {
        holder.itemTitle.text = title[position]
        holder.itemDesc.text = desc[position]
        holder.itemillust.setImageResource(image[position])
    }
}