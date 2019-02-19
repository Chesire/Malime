package com.chesire.malime.kitsu.interceptors

import com.chesire.malime.kitsu.AuthProvider
import com.chesire.malime.kitsu.api.auth.KitsuAuthService
import com.chesire.malime.kitsu.api.auth.LoginResponse
import com.chesire.malime.kitsu.api.auth.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

// we must use an interceptor as kitsu doesn't return a 401, only 403.
class AuthRefreshInterceptor(
    private val provider: AuthProvider,
    private val auth: KitsuAuthService
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val response = chain.proceed(originRequest)

        return if (!response.isSuccessful && response.code() == 403) {
            val authResponse = runBlocking { getNewAuth(provider.refreshToken) }
            if (authResponse.isSuccessful && authResponse.body() != null) {
                authResponse.body()?.let {
                    provider.apply {
                        accessToken = it.accessToken
                        refreshToken = it.refreshToken
                    }

                    chain.proceed(
                        originRequest.newBuilder()
                            .header("Authorization", "Bearer ${it.accessToken}")
                            .build()
                    )
                } ?: generateFailureResponse()
            } else {
                generateFailureResponse()
            }
        } else {
            response
        }
    }

    private suspend fun getNewAuth(refreshToken: String): retrofit2.Response<LoginResponse> {
        return auth.refreshAccessTokenAsync(RefreshTokenRequest(refreshToken)).await()
    }

    private fun generateFailureResponse(): Response {
        // If there is an auth failure report a 401 error for the app to logout with
        return Response
            .Builder()
            .code(401)
            .build()
    }
}
