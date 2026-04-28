package com.kholodkov.coinmonitor.domain.usecase.auth

import com.kholodkov.coinmonitor.domain.model.user.User
import com.kholodkov.coinmonitor.domain.repository.UserRepository
import javax.inject.Inject

class EnsureUserExistsUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(user: User) = runCatching {
        userRepository.ensureUserExists(user)
    }
}