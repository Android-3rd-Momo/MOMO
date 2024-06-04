package kr.nbc.momo.presentation.onboarding.term

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.nbc.momo.R

class TermViewModel : ViewModel() {
    private val _terms = MutableLiveData<List<Term>>()
    val terms: LiveData<List<Term>> get() = _terms

    init {
        loadTerms()
    }

    private fun loadTerms() {

        val termList = listOf(
            Term("@string/Term1", ".", false),
            Term("@string/Term2", "@string/See", false),
            Term("@string/Term3", "@string/See", false),
            Term("@string/Term4", ".", false)
        )
        _terms.value = termList
    }
}

// Term.kt
data class Term(val title: String, val look: String, var isAccepted: Boolean)