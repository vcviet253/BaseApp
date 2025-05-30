package com.example.mealplanner.presentation.navigation

object AppDestinations {
    const val TEST_LIST_ROUTE = "testList" // Screen showing list of tests
    const val MAP_LABELING_ROUTE = "mapLabeling"
    const val MAP_LABELING_ARG_TEST_ID = "testId"

    // Route definition for MapLabelingScreen expecting a testId argument
    const val MAP_LABELING_FULL_ROUTE = "$MAP_LABELING_ROUTE/{$MAP_LABELING_ARG_TEST_ID}"

    const val SAMPLE_ANSWER_SPEAKING_ROUTE = "sampleAnswerSpeaking"

    const val WEATHER_ROUTE = "weather"

    const val HOME_ROUTE = "home"

    const val MOVIE_DETAIL_ROUTE_BASE = "movie_detail" // Phần gốc của route
    const val MOVIE_DETAIL_ARG_SLUG = "slug"          // Tên argument
    const val MOVIE_DETAIL_ROUTE = "$MOVIE_DETAIL_ROUTE_BASE/{$MOVIE_DETAIL_ARG_SLUG}"
}