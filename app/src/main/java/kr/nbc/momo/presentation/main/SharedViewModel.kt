package kr.nbc.momo.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.nbc.momo.presentation.group.model.GroupModel

class SharedViewModel: ViewModel() {
    private val _groupName: MutableLiveData<String> = MutableLiveData()
    val groupName: LiveData<String> get() = _groupName

    fun getGroupName(groupName: String) {
        _groupName.value = groupName
    }

}