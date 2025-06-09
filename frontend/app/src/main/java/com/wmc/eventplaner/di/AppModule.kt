package com.wmc.eventplaner.di

import com.wmc.eventplaner.data.ApiService
import com.wmc.eventplaner.data.RemoteRepository
import com.wmc.eventplaner.util.Const
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideBaseUrl() = "http://localhost/api/"
    fun provideToken() = Const.authToken

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            // Custom header interceptor
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Accept", "application/json") // Example header
                    .header("Authorization", "Bearer ${provideToken()}") // Dynamic token can be injected
                    .method(original.method, original.body)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            // Logging interceptor
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    fun provideRepository(apiService: ApiService): RemoteRepository =
        RemoteRepository(apiService)
}
//class AuthInterceptor @Inject constructor(
//    val token: String
//) : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val token = token // get from DataStore or memory
//        val newRequest = chain.request().newBuilder()
//            .addHeader("Authorization", "Bearer $token")
//            .build()
//        return chain.proceed(newRequest)
//    }
//}
