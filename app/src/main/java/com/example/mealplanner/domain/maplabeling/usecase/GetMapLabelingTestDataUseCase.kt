package com.example.mealplanner.domain.maplabeling.usecase

import com.example.mealplanner.common.Resource
import com.example.mealplanner.domain.maplabeling.model.MapLabelingTestData
import com.example.mealplanner.domain.maplabeling.repository.MapLabelingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// --- Use Case Lấy Dữ Liệu Test ---
class GetMapLabelingTestDataUseCase @Inject constructor(
    private val repository: MapLabelingRepository // Inject interface
) {
    operator fun invoke(testId: Long): Flow<Resource<MapLabelingTestData>> {
        return repository.getMapLabelingTestData(testId)
    }
}