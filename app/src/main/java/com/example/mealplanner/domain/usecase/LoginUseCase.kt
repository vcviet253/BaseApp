package com.example.mealplanner.domain.usecase

import com.example.mealplanner.common.Resource
import com.example.mealplanner.common.UserSession
import com.example.mealplanner.data.preferences.UserPreferences
import com.example.mealplanner.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.math.log
import retrofit2.HttpException


class LoginUseCase @Inject constructor(private val loginRepository: LoginRepository,
    private val userPreferences: UserPreferences
    ){
    operator fun invoke(username: String, password: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = loginRepository.login(username, password)
            userPreferences.saveToken(response.token)
            userPreferences.saveUserId(username)
            UserSession.userId = username
            //UserSession.initFromPreferences(userPreferences)
            emit(Resource.Success(data = response.token))
        } catch(e: HttpException) {
          //  emit(Resource.Error("Login failed : ${e.message}"))
            when (e.code()) {
                401 ->  emit(Resource.Error("Invalid credentials"))
                500 -> emit(Resource.Error("Server error. Try again later"))
                else -> emit(Resource.Error("Unknown error : ${e.message}"))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Cannot connect to server. Check your internet connection and try again"))
        } catch (e: SocketTimeoutException) {
            emit(Resource.Error("Connection timed out. Try again later"))
        } catch (e: Exception) {
            emit(Resource.Error("Unknown error: ${e.message}"))
        }
    }
}