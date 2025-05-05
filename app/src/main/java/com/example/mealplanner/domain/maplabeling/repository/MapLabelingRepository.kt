package com.example.mealplanner.domain.maplabeling.repository

import com.example.mealplanner.core.common.Resource
import com.example.mealplanner.domain.maplabeling.model.MapLabelingTestData
import kotlinx.coroutines.flow.Flow

interface MapLabelingRepository {
    /**
     * Lấy dữ liệu chi tiết cho một bài test Map Labeling dựa vào ID.
     * Trả về Flow chứa Result để xử lý thành công/thất bại.
     */
    fun getMapLabelingTestData(testId: Long): Flow<Resource<MapLabelingTestData>>
}

