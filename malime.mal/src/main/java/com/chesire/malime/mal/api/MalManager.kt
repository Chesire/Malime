package com.chesire.malime.mal.api

import com.chesire.malime.core.api.AuthApi
import com.chesire.malime.core.api.LibraryApi
import com.chesire.malime.core.api.SearchApi
import com.chesire.malime.core.flags.ItemType
import com.chesire.malime.core.flags.SeriesStatus
import com.chesire.malime.core.flags.Subtype
import com.chesire.malime.core.flags.UserSeriesStatus
import com.chesire.malime.core.models.AuthModel
import com.chesire.malime.core.models.MalimeModel
import com.chesire.malime.mal.models.Anime
import com.chesire.malime.mal.models.Manga
import com.chesire.malime.mal.models.response.SearchForAnimeResponse
import com.chesire.malime.mal.models.response.SearchForMangaResponse
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Provides a manager to interact with the MyAnimeList API.
 */
@Suppress("TooManyFunctions")
class MalManager(
    private val api: MalApi,
    private val username: String
) : AuthApi, LibraryApi, SearchApi {
    /**
     * Verifies a users credentials.
     * <p>
     * Since there is no "login" or auth token to store, this just verifies that the credentials
     * entered are correct.
     *
     * @return [Single<AuthModel>] with the success and failure states, will be an empty AuthModel
     */
    override fun login(username: String, password: String): Single<AuthModel> {
        return Single.create {
            val callResponse = api.loginToAccount()
            val response = callResponse.execute()

            if (response.isSuccessful) {
                Timber.i("Login successful")
                it.onSuccess(AuthModel("", "", 0, "mal"))
            } else {
                Timber.e(Throwable(response.message()), "Error with the login method")
                it.tryOnError(Throwable(response.message()))
            }
        }
    }

    // MyAnimeList does not support auth tokens currently
    override fun getNewAuthToken(refreshToken: String): Single<AuthModel> = Single.never()

    override fun getUserId(): Single<Int> {
        return Single.create {
            val callResponse = api.loginToAccount()
            val response = callResponse.execute()

            if (response.isSuccessful && response.body() != null) {
                Timber.i("Login successful")
                it.onSuccess(response.body()!!.id!!)
            } else {
                Timber.e(Throwable(response.message()), "Error with the login method")
                it.tryOnError(Throwable(response.message()))
            }
        }
    }

    override fun getUserLibrary(): Observable<List<MalimeModel>> {
        return Observable.zip(
            getUserAnime(),
            getUserManga(),
            BiFunction { anime: List<Anime>, manga: List<Manga> ->
                val libraryItems = ArrayList<MalimeModel>()
                anime.forEach {
                    libraryItems.add(
                        MalimeModel(
                            seriesId = it.seriesAnimeDbId!!,
                            userSeriesId = it.myId!!,
                            type = ItemType.Anime,
                            subtype = Subtype.Unknown,
                            slug = it.seriesTitle!!,
                            title = it.seriesTitle!!,
                            seriesStatus = SeriesStatus.getStatusForMalId(it.seriesStatus!!),
                            userSeriesStatus = UserSeriesStatus.getStatusForMalId(it.myStatus!!),
                            progress = it.myWatchedEpisodes!!,
                            totalLength = it.seriesEpisodes!!,
                            posterImage = it.seriesImage!!,
                            coverImage = it.seriesImage!!,
                            nsfw = false,
                            startDate = it.myStartDate!!,
                            endDate = it.myFinishDate!!
                        )
                    )
                }
                manga.forEach {
                    libraryItems.add(
                        MalimeModel(
                            seriesId = it.seriesMangaDbId!!,
                            userSeriesId = it.myId!!,
                            type = ItemType.Manga,
                            subtype = Subtype.Unknown,
                            slug = it.seriesTitle!!,
                            title = it.seriesTitle!!,
                            seriesStatus = SeriesStatus.getStatusForMalId(it.seriesStatus!!),
                            userSeriesStatus = UserSeriesStatus.getStatusForMalId(it.myStatus!!),
                            progress = it.myReadChapters!!,
                            totalLength = it.seriesChapters!!,
                            posterImage = it.seriesImage!!,
                            coverImage = it.seriesImage!!,
                            nsfw = false,
                            startDate = it.myStartDate!!,
                            endDate = it.myFinishDate!!
                        )
                    )
                }
                libraryItems
            }
        )
    }

    override fun addItem(item: MalimeModel): Single<MalimeModel> {
        return Single.create {
            val updateString = createUpdateString(item, 0, UserSeriesStatus.Current)
            val callResponse = if (item.type == ItemType.Anime) {
                api.addAnime(item.seriesId, updateString)
            } else {
                api.addManga(item.seriesId, updateString)
            }
            val response = callResponse.execute()

            if (response.isSuccessful) {
                Timber.i("Item [${item.title}] added")
                it.onSuccess(item.copy().apply { userSeriesStatus = UserSeriesStatus.Current })
            } else {
                Timber.e(
                    Throwable(response.message()),
                    "Error adding item [%s] - %s",
                    item.title,
                    response.errorBody()
                )
                it.tryOnError(Throwable(response.message()))
            }
        }
    }

    override fun deleteItem(item: MalimeModel): Single<MalimeModel> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun updateItem(
        item: MalimeModel,
        newProgress: Int,
        newStatus: UserSeriesStatus
    ): Single<MalimeModel> {
        return Single.create {
            val updateString = createUpdateString(item, newProgress, newStatus)
            val callResponse = if (item.type == ItemType.Anime) {
                api.updateAnime(item.seriesId, updateString)
            } else {
                api.updateManga(item.seriesId, updateString)
            }

            val response = callResponse.execute()
            if (response.isSuccessful) {
                Timber.i("Item [%s] has updated to progress [%d]", item.title, item.progress)
                item.progress = newProgress
                item.userSeriesStatus = newStatus
                it.onSuccess(item)
            } else {
                Timber.e(
                    Throwable(response.message()),
                    "Error Updating item [%s]",
                    item.title
                )
                it.tryOnError(Throwable(response.message()))
            }
        }
    }

    @Suppress("ComplexMethod", "UnsafeCast")
    override fun searchForSeriesWith(
        title: String,
        type: ItemType
    ): Single<List<MalimeModel>> {
        return Single.create { subscriber ->
            val callResponse = if (type == ItemType.Anime) {
                api.searchForAnime(title)
            } else {
                api.searchForManga(title)
            }
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody == null) {
                    subscriber.tryOnError(Throwable(response.message()))
                } else {
                    val list = if (type == ItemType.Anime) {
                        (responseBody as SearchForAnimeResponse).entries
                    } else {
                        (responseBody as SearchForMangaResponse).entries
                    }

                    val responseList = list.map {
                        MalimeModel(
                            seriesId = it.id!!,
                            userSeriesId = 0,
                            type = type,
                            subtype = Subtype.Unknown,
                            slug = it.title!!,
                            title = it.title,
                            seriesStatus = SeriesStatus.Unknown,
                            userSeriesStatus = UserSeriesStatus.Unknown,
                            progress = 0,
                            totalLength = if (type == ItemType.Anime) {
                                it.episodes!!
                            } else {
                                it.chapters!!
                            },
                            posterImage = it.image!!,
                            coverImage = it.image,
                            nsfw = false,
                            startDate = it.start_date!!,
                            endDate = it.end_date!!
                        )
                    }

                    subscriber.onSuccess(responseList)
                }
            } else {
                subscriber.tryOnError(Throwable(response.message()))
            }
        }
    }

    private fun getUserAnime(): Observable<List<Anime>> {
        return Observable.create { subscriber ->
            val callResponse = api.getAllAnime(username)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                Timber.i("Get all anime successful")

                val responseBody = response.body()
                if (responseBody?.myInfo == null) {
                    subscriber.tryOnError(Throwable(response.message()))
                } else {
                    subscriber.onNext(responseBody.animeList!!)
                    subscriber.onComplete()
                }
            } else {
                Timber.e(Throwable(response.message()))
                subscriber.tryOnError(Throwable(response.message()))
            }
        }
    }

    private fun getUserManga(): Observable<List<Manga>> {
        return Observable.create { subscriber ->
            val callResponse = api.getAllManga(username)
            val response = callResponse.execute()

            if (response.isSuccessful) {
                Timber.i("Get all manga successful")

                val responseBody = response.body()
                if (responseBody?.myInfo == null) {
                    subscriber.tryOnError(Throwable(response.message()))
                } else {
                    subscriber.onNext(responseBody.mangaList!!)
                    subscriber.onComplete()
                }
            } else {
                Timber.e(Throwable(response.message()))
                subscriber.tryOnError(Throwable(response.message()))
            }
        }
    }

    private fun createUpdateString(
        item: MalimeModel,
        newProgress: Int,
        newStatus: UserSeriesStatus
    ): String {
        // Need to test if some of these can be removed...
        // I'm not sure if it has to be in the correct order
        // so for now it won't be... if it has to be the correct order is
        /*
        Anime:
        "<entry>" +
                "<episode>$newProgress</episode>" +
                "<status>$status</status>" +
                "<score>$score</score>" +
                "<storage_type></storage_type>" +
                "<storage_value></storage_value>" +
                "<times_rewatched></times_rewatched>" +
                "<rewatch_value></rewatch_value>" +
                "<date_start></date_start>" +
                "<date_finish>$dateFinish</date_finish>" +
                "<priority></priority>" +
                "<enable_discussion></enable_discussion>" +
                "<enable_rewatching></enable_rewatching>" +
                "<comments></comments>" +
                "<tags></tags>" +
                "</entry>"

         Manga:
         "<entry>" +
                "<chapter>$chapter</chapter>" +
                "<volume>$volume</volume>" +
                "<status>$status</status>" +
                "<score>$score</score>" +
                "<times_reread></times_reread>" +
                "<reread_value></reread_value>" +
                "<date_start></date_start>" +
                "<date_finish>$dateFinish</date_finish>" +
                "<priority></priority>" +
                "<enable_discussion></enable_discussion>" +
                "<enable_rewatching></enable_rewatching>" +
                "<comments></comments>" +
                "<scan_group></scan_group>" +
                "<tags></tags>" +
                "</entry>"
         */

        val currentTime = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            .format(Calendar.getInstance().time)
        return "<entry>" +
                "<priority></priority>" +
                "<enable_discussion></enable_discussion>" +
                "<enable_rewatching></enable_rewatching>" +
                "<comments></comments>" +
                "<tags></tags>" +
                "<score></score>" +
                "<status>${newStatus.malId}</status>" +
                if (item.type == ItemType.Anime) {
                    "<episode>$newProgress</episode>" +
                            "<times_rewatched></times_rewatched>" +
                            "<rewatch_value></rewatch_value>" +
                            "<storage_type></storage_type>" +
                            "<storage_value></storage_value>"
                } else {
                    "<chapter>$newProgress</chapter>" +
                            "<volume></volume>" +
                            "<times_reread></times_reread>" +
                            "<reread_value></reread_value>" +
                            "<scan_group></scan_group>"
                } +
                if (item.progress == 0 && newProgress > 0) {
                    "<date_start>$currentTime</date_start>"
                } else {
                    "<date_start>${item.startDate}</date_start>"
                } +
                if (item.progress < item.totalLength && newProgress == item.totalLength) {
                    "<date_finish>$currentTime</date_finish>"
                } else {
                    "<date_finish>${item.endDate}</date_finish>"
                } +
                "</entry>"
    }
}
