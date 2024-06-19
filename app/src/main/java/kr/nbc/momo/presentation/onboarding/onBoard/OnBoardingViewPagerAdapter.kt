package kr.nbc.momo.presentation.onboarding.onBoard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.ItemPageBinding

class OnBoardingViewPagerAdapter(
    private val title: List<String>,
    private val desc: List<String>,
    private val image: List<Int>
) : RecyclerView.Adapter<OnBoardingViewPagerAdapter.Pager2ViewHolder>() {

    inner class Pager2ViewHolder(private val binding: ItemPageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: String, desc: String, image: Int) {
            binding.TextViewTitle.text = title
            binding.TextViewDesc.text = desc
            binding.ImageViewIllust.setImageResource(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Pager2ViewHolder {
        val binding = ItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Pager2ViewHolder(binding)
    }

    override fun getItemCount(): Int = title.size

    override fun onBindViewHolder(holder: Pager2ViewHolder, position: Int) {
        holder.bind(title[position], desc[position], image[position])
    }
}

