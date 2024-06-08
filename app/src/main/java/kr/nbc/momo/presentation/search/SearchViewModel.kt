package kr.nbc.momo.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.nbc.momo.domain.usecase.SearchUseCase
import kr.nbc.momo.presentation.UiState
import kr.nbc.momo.presentation.group.mapper.toGroupModel
import kr.nbc.momo.presentation.group.model.GroupModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {
    private val _searchResult: MutableStateFlow<UiState<List<GroupModel>>> =
        MutableStateFlow(UiState.Loading)
    val searchResult: StateFlow<UiState<List<GroupModel>>> get() = _searchResult

    fun getSearchResult(category: String, works: String, query: String) {
        viewModelScope.launch {
            try {
                _searchResult.value = UiState.Success(searchUseCase.invoke(category, works, query).map { it.toGroupModel() })
            } catch (e: Exception) {
                _searchResult.value = UiState.Error(e.toString())
            }
        }
    }
}