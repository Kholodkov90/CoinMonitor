package com.kholodkov.coinmonitor.domain.usecase

import com.kholodkov.coinmonitor.domain.model.User
import com.kholodkov.coinmonitor.domain.repository.UserRepository
import javax.inject.Inject

class EnsureUserExistsUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(user: User) = runCatching {
        userRepository.ensureUserExists(user)
    }
}