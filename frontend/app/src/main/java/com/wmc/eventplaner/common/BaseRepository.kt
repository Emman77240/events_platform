package com.wmc.eventplaner.common

import retrofit2.HttpException
import java.io.IOException

import com.google.gson.Gson
import com.wmc.eventplaner.data.dto.ErrorResponse
open class BaseRepository {

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return try {
            val result = apiCall()
            Resource.Success(result)
        } catch (e: HttpException) {
            val errorMessage = try {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.responseMessage ?: "Something went wrong!"
            } catch (ex: Exception) {
                "Something went wrong!"
            }
            Resource.Error(errorMessage)
        } catch (e: IOException) {
            Resource.Error("Network error. Please check your connection.")
        } catch (e: Exception) {
            Resource.Error("Unexpected error occurred.")
        }
    }
}

