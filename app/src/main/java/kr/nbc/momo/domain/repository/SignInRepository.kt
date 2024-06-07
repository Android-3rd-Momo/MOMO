package kr.nbc.momo.domain.repository

import kr.nbc.momo.domain.model.SignInEntity

interface SignInRepository {
    suspend fun login(email: String, password: String): SignInEntity
}