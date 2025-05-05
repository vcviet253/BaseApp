package com.example.mealplanner.core.common

import com.example.mealplanner.data.preferences.UserPreferences

object UserSession {
    var userId: String? =  null

    fun initFromPreferences(userPreferences: UserPreferences) {
        userId = userPreferences.getUserId()
    }

    fun clear() {
        userId = null
    }
}