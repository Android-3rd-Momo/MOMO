package kr.nbc.momo.presentation.group.read

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.GetGroupUriUseCase
import kr.nbc.momo.domain.usecase.ReadGroupUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject
@HiltViewModel
class ReadGroupViewModel  @Inject constructor(
    private val readGroupUseCase: ReadGroupUseCase,
//    private val getGroupUriUseCase: GetGroupUriUseCase
) : ViewModel() {
    private val _readGroup =  MutableStateFlow<UiState<GroupModel>>(UiState.Loading)
    val readGroup: StateFlow<UiState<GroupModel>> get() = _readGroup

//    private val _getImage =  MutableStateFlow<UiState<Uri>>(UiState.Loading)
//    val getImage: StateFlow<UiState<Uri>> get() = _getImage

    fun readGroup(groupId: String) {
        viewModelScope.launch {
            _readGroup.value = UiState.Loading

            readGroupUseCase.invoke(groupId)
                .catch { e ->
                    _readGroup.value = UiState.Error(e.toString())
                }
                .collect { data ->
                    _readGroup.value = UiState.Success(data.toGroupModel())
                }
        }
    }
//
//    fun getImage(groupName: String) {
//        viewModelScope.launch {
//            _getImage.value = UiState.Loading
//
//            getGroupUriUseCase.invoke(groupName)
//                .catch { e ->
//                    _getImage.value = UiState.Error(e.toString())
//                }
//                .collect { data ->
//                    _getImage.value = UiState.Success(data)
//                }
//        }
//    }
}
