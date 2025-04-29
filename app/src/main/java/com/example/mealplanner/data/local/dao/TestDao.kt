package com.example.mealplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mealplanner.data.local.entity.AnswerOptionEntity
import com.example.mealplanner.data.local.entity.QuestionEntity
import com.example.mealplanner.data.local.entity.TestEntity
import com.example.mealplanner.data.local.model.TestType
import com.example.mealplanner.data.local.relation.TestAndQuestions
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) cho các bảng liên quan đến bài test.
 * Cung cấp các phương thức để tương tác với database (CRUD).
 */
@Dao
interface TestDao {

    // --- Các phương thức INSERT ---

    /**
     * Thêm một TestEntity vào database.
     * Nếu testId đã tồn tại, nó sẽ được thay thế (REPLACE).
     * Trả về rowId của dòng vừa được thêm.
     * Dùng suspend vì là hoạt động I/O.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestEntity): Long

    /**
     * Thêm một danh sách QuestionEntity.
     * Các câu hỏi có questionId trùng lặp sẽ được thay thế.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    /**
     * Thêm một danh sách AnswerOptionEntity.
     * Các lựa chọn/đáp án có optionId trùng lặp sẽ được thay thế.
     * Lưu ý: Cách điền dữ liệu vào đây phụ thuộc vào TestType.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswerOptions(options: List<AnswerOptionEntity>)

    // --- Các phương thức QUERY ---
    /**
     * Lấy một bài Test đầy đủ (bao gồm Questions và AnswerOptions liên quan) dựa trên testId.
     * Sử dụng @Transaction để đảm bảo các truy vấn liên quan được thực hiện nhất quán.
     * Trả về Flow<TestAndQuestions?>: Flow để tự động cập nhật khi dữ liệu thay đổi,
     * trả về null nếu không tìm thấy testId.
     */
    @Transaction // Rất quan trọng cho việc query các mối quan hệ
    @Query("SELECT * FROM tests WHERE id = :testId")
    fun getTestAndQuestionsById(testId: Long): Flow<TestAndQuestions?>

    /**
     * Lấy danh sách tất cả các bài Test đầy đủ thuộc một loại TestType cụ thể.
     * Sắp xếp theo tiêu đề.
     * Trả về Flow<List<TestAndQuestions>>.
     */
    @Transaction
    @Query("SELECT * FROM tests WHERE testType = :testType ORDER BY title ASC")
    fun getTestsAndQuestionsByType(testType: TestType): Flow<List<TestAndQuestions>>

    /**
     * Lấy danh sách chỉ các TestEntity (thông tin cơ bản, không kèm câu hỏi/đáp án)
     * thuộc một loại TestType cụ thể. Hữu ích để hiển thị danh sách các bài test.
     */
    @Query("SELECT * FROM tests WHERE testType = :testType ORDER BY title ASC")
    fun getTestsByTypeSummary(testType: TestType): Flow<List<TestEntity>>

    /**
     * Lấy danh sách tất cả các TestEntity (thông tin cơ bản).
     */
    @Query("SELECT * FROM tests ORDER BY title ASC")
    fun getAllTestsSummary(): Flow<List<TestEntity>>


    // --- Các phương thức DELETE (Ví dụ - Thêm nếu cần) ---

    /**
     * Xóa một bài test cụ thể dựa trên testId.
     * Do đã thiết lập `onDelete = ForeignKey.CASCADE` trong QuestionEntity và AnswerOptionEntity,
     * các questions và options liên quan sẽ tự động bị xóa theo.
     */
    @Query("DELETE FROM tests WHERE id = :testId")
    suspend fun deleteTestById(testId: Long)

    /**
     * Xóa tất cả các bài test. Cẩn thận khi sử dụng!
     */
    @Query("DELETE FROM tests")
    suspend fun deleteAllTests()

    // --- Các phương thức UPDATE (Ví dụ - Thêm nếu cần) ---

    // @Update
    // suspend fun updateTest(test: TestEntity)

    // --- Ghi chú về Transaction khi Insert toàn bộ Test ---
    // Việc insert một bài test mới hoàn chỉnh (Test + Questions + Options)
    // thường phức tạp hơn việc chỉ gọi 3 hàm insert riêng lẻ, vì bạn cần lấy
    // testId được tạo tự động để gán cho questions, và questionId được tạo tự động
    // để gán cho options. Logic này thường được xử lý tốt hơn ở tầng Repository
    // bằng cách gọi các hàm DAO riêng lẻ trong một transaction của Repository.
    // Ví dụ dưới đây chỉ mang tính minh họa và giả định ID đã được set đúng:
    /*
    @Transaction
    suspend fun insertCompleteTest(test: TestEntity, questions: List<QuestionEntity>, options: List<AnswerOptionEntity>) {
        insertTest(test)
        insertQuestions(questions)
        insertAnswerOptions(options)
    }
    */
}