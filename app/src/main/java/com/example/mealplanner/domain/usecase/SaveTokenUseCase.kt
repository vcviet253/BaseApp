package com.example.mealplanner.domain.usecase

import com.example.mealplanner.domain.repository.TokenRepository
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(private val tokenRepository: TokenRepository) {
    suspend operator fun invoke(token: String) {
        tokenRepository.saveToken(token)
    }
}