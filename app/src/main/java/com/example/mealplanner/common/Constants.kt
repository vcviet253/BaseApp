package com.example.mealplanner.common

object Constants {
    const val PARAM_MEAL_NAME = "mealName"
    const val BASE_URL = "https://www.themealdb.com/"
    const val GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/"
    const val SERVER_BASE_URL =  "http://192.168.1.100:8000/"  // Must end with /

    const val PROMPT =
        "Create a short story based on the provided images. Imagine these are snapshots from someone's day or life. Describe the characters, the scenes, and events that happen in a logical, emotional, and vivid way. \"Describe the story in the chronological order of the images. Make the story feel real.\n"
}