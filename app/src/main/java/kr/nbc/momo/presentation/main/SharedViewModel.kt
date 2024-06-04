package kr.nbc.momo.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kr.nbc.momo.presentation.group.model.GroupModel

class SharedViewModel: ViewModel() {
    private val _groupName: MutableLiveData<String> = MutableLiveData()
    val groupName: LiveData<String> get() = _groupName

    fun getGroupName(groupName: String) {
        _groupName.value = groupName
    }



    private val _groupIdToGroupChat: MutableStateFlow<String?> = MutableStateFlow(null)
    val groupIdToGroupChat: StateFlow<String?> get() = _groupIdToGroupChat

    fun setGroupIdToGroupChat(groupId: String){
        _groupIdToGroupChat.value = groupId
    }
    fun removeGroupIdToGroupChat(){
        _groupIdToGroupChat.value = null
    }

}