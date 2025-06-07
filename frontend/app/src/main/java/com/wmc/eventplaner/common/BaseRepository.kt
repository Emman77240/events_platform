package com.wmc.eventplaner.common

import retrofit2.HttpException
import java.io.IOException

abstract class BaseRepository {

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return try {
            val response = apiCall()
            Resource.Success(response)
        } catch (e: HttpException) {
            Resource.Error(e.message ?: "An unexpected error occurred", null)
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection.", null)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Something went wrong", null)
        }
    }
}
