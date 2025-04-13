package com.example.mealplanner.domain.usecase

import com.example.mealplanner.domain.model.Message
import com.example.mealplanner.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(private val repository: ChatRepository) {
    suspend operator fun invoke(message: Message) {
        repository.sendMessage(message)
    }
}