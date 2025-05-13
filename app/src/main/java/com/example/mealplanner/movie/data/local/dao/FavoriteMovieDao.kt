package com.example.mealplanner.movie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mealplanner.movie.data.local.entity.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface FavoriteMovieDao {
    // Chèn phim yêu thích. Nếu đã tồn tại (dựa vào PrimaryKey), bỏ qua hoặc thay thế.
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Hoặc REPLACE nếu muốn cập nhật timestamp
    suspend fun insertFavorite(movie: FavoriteMovieEntity)

    // Xóa phim khỏi danh sách yêu thích dựa trên ID
    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun deleteFavoriteById(movieId: String)

    // Kiểm tra xem một phim có ID cụ thể có nằm trong danh sách yêu thích không
    // Trả về Flow<Boolean> để UI/ViewModel có thể reactive với trạng thái này
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId LIMIT 1)")
    fun isFavorite(movieId: String): Flow<Boolean> // Hoặc Flow<Int> và check > 0

    // Lấy danh sách TẤT CẢ phim yêu thích, sắp xếp theo thời gian đánh dấu
    // Trả về Flow<List<FavoriteMovieEntity>> để UI reactive
    @Query("SELECT * FROM favorite_movies ORDER BY favoritedTimestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>

    // Nếu dùng Paging 3 cho danh sách yêu thích offline
    // @Query("SELECT * FROM favorite_movies ORDER BY favoritedTimestamp DESC")
    // fun getAllFavoritesPaged(): PagingSource<Int, FavoriteMovieEntity>

    /**
     * Checks if a movie is favorited and toggles its status within a transaction.
     * If currently favorited, it deletes it.
     * If not currently favorited, it inserts it.
     * @param movieId The ID of the movie to toggle.
     * @param movieEntityToInsert The FavoriteMovieEntity to insert if the movie is not currently favorited.
     * This should be non-null if the movie is expected to be inserted.
     * @return True if the movie is favorited AFTER the operation, false otherwise.
     */
    @Transaction // <-- Đánh dấu phương thức này là một Transaction
    suspend fun toggleFavoriteTransaction(movieId: String, movieEntityToInsert: FavoriteMovieEntity?): Boolean {
        // Kiểm tra trạng thái yêu thích HIỆN TẠI bên trong Transaction.
        // Sử dụng .first() vẫn OK ở đây vì nó chỉ lấy 1 giá trị từ Flow trong phạm vi suspend/transaction.
        val isCurrentlyFavorite = isFavorite(movieId).first()

        return if (isCurrentlyFavorite) {
            // Nếu đang yêu thích (true), xóa khỏi DB
            deleteFavoriteById(movieId)
            false // Trạng thái cuối cùng là không yêu thích
        } else {
            // Nếu chưa yêu thích (false), thêm vào DB
            // Đảm bảo movieEntityToInsert không null trước khi chèn
            if (movieEntityToInsert != null) {
                insertFavorite(movieEntityToInsert)
                true // Trạng thái cuối cùng là yêu thích
            } else {
                // Trường hợp không mong muốn xảy ra nếu logic Repository đúng
                // ( Repository chỉ gọi insert khi isCurrentlyFavorite là false, và phải cung cấp Entity)
                false // Giữ nguyên trạng thái không yêu thích nếu thiếu data insert
            }
        }
    }
}