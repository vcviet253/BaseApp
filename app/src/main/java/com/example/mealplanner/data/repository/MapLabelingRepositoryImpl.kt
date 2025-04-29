package com.example.mealplanner.data.repository

import com.example.mealplanner.common.Resource
import com.example.mealplanner.data.local.dao.TestDao
import com.example.mealplanner.data.mapper.toDomainModel
import com.example.mealplanner.domain.maplabeling.model.MapLabelingTestData
import com.example.mealplanner.domain.maplabeling.repository.MapLabelingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton // Đánh dấu Singleton nếu muốn dùng chung instance Repository
class MapLabelingRepositoryImpl @Inject constructor(
    private val testDao: TestDao // Inject DAO
    // Không cần inject Mapper nếu dùng extension function
) : MapLabelingRepository {

    override fun getMapLabelingTestData(testId: Long): Flow<Resource<MapLabelingTestData>> {
        return testDao.getTestAndQuestionsById(testId) // Gọi DAO lấy TestAndQuestions Flow
            .map { testAndQuestions ->
                // Map từ Data Model (Entity/Relation POJO) sang Domain Model
                val domainData = testAndQuestions?.toDomainModel()
                if (domainData != null) {
                    Resource.Success(domainData) // Trả về Success nếu map thành công
                } else {
                    // Trả về Error nếu không tìm thấy Test hoặc không map được
                    Resource.Error(NoSuchElementException("Test data not found or invalid for id: $testId").toString())
                }
            }
            .catch { e ->
                // Bắt lỗi từ Flow (ví dụ: lỗi database)
                emit(Resource.Error(Exception("Database error", e).toString()))
            }
            .flowOn(Dispatchers.IO) // Đảm bảo truy vấn DB chạy trên IO thread
    }

    // Implement các phương thức khác của Repository ở đây nếu có
}