package com.example.mealplanner.core.common

object Constants {
    const val PARAM_MEAL_NAME = "mealName"
    const val GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/"
    const val SERVER_BASE_URL =  "http://10.0.2.2:8000/"  // Must end with /
    const val WEATHER_API_BASE_URL = "http://api.weatherapi.com/"
    const val WEATHER_API_KEY = "0e382cd0f7c94310abe72912250605"

    const val MOVIE_API_BASE_URL = "https://phimapi.com/"

    const val HOST = "10.0.2.2"
    const val PORT = 8000
    const val BASE_URL = "http://$HOST:$PORT/"
    const val WS_URL = "ws://$HOST:$PORT/ws"

    const val PROMPT =
        "Create a short story based on the provided images. Imagine these are snapshots from someone's day or life. Describe the characters, the scenes, and events that happen in a logical, emotional, and vivid way. \"Describe the story in the chronological order of the images. Make the story feel real.\n"
}