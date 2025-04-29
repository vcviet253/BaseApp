package com.example.mealplanner.presentation.navigation

object AppDestinations {
    const val TEST_LIST_ROUTE = "testList" // Screen showing list of tests
    const val MAP_LABELING_ROUTE = "mapLabeling"
    const val MAP_LABELING_ARG_TEST_ID = "testId"
    // Route definition for MapLabelingScreen expecting a testId argument
    const val MAP_LABELING_FULL_ROUTE = "$MAP_LABELING_ROUTE/{$MAP_LABELING_ARG_TEST_ID}"
}