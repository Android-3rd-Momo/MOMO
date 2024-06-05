package kr.nbc.momo.presentation.onboarding.term

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.nbc.momo.databinding.ItemTermselectBinding

class TermRecyclerViewAdapter(private val terms: List<Term>) : RecyclerView.Adapter<TermRecyclerViewAdapter.TermViewHolder>() {

    class TermViewHolder(val binding: ItemTermselectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTermselectBinding.inflate(layoutInflater, parent, false)
        return TermViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TermViewHolder, position: Int) {
        val term = terms[position]
        holder.binding.term = term
        holder.binding.executePendingBindings()

        holder.binding.root.setOnClickListener {
            term.isAccepted = !term.isAccepted
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = terms.size
}