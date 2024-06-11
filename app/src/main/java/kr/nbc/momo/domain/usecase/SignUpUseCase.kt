package kr.nbc.momo.domain.usecase

import kr.nbc.momo.domain.model.UserEntity
import kr.nbc.momo.domain.repository.UserRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(email: String, password: String, user: UserEntity): UserEntity {
//        if (userRepository.isUserIdDuplicate(user.userId)) {
//            throw Exception("이미 사용 중인 아이디입니다.")
//        }
//        if (userRepository.isUserNumberDuplicate(user.userNumber)) {
//            throw Exception("이미 사용 중인 전화번호입니다.")
//        }
        return userRepository.signUpUser(email, password, user)
    }
}