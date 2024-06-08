package kr.nbc.momo.presentation.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kr.nbc.momo.databinding.SpinnerItemDropdownBinding
import kr.nbc.momo.databinding.SpinnerItemSelectedBinding

class SearchSpinnerAdapter(
    context: Context,
    items: List<String>,
): ArrayAdapter<String>(context, 0, items) {
    private var itemList = items

    override fun getItem(position: Int): String {
        return itemList[position]
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: SpinnerItemSelectedBinding
        val view: View

        if (convertView == null){
            binding = SpinnerItemSelectedBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        }else{
            binding = convertView.tag as SpinnerItemSelectedBinding
            view = convertView
        }

        val item = getItem(position)?:""
        binding.tvSpinner.text = item

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: SpinnerItemDropdownBinding
        val view: View

        if (convertView == null){
            binding = SpinnerItemDropdownBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        }else{
            binding = convertView.tag as SpinnerItemDropdownBinding
            view = convertView
        }

        val item = getItem(position)?:""
        binding.tvSpinner.text = item

        return view
    }

}
