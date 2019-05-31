package com.chesire.malime.kitsu.api.user

import com.chesire.malime.core.models.UserModel
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface KitsuUserService {
    /**
     * Gets the user details, requires authentication to be set.
     */
    @GET("api/edge/users?filter[self]=true&fields[users]=id,name,slug,ratingSystem,avatar,coverImage")
    fun getUserDetailsAsync(): Deferred<Response<UserModel>>
}