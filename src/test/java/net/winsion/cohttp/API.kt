package net.winsion.cohttp

import net.winsion.cohttp.markers.*

interface API {
    @GET("/v2/book/{id}")
    suspend fun book(@Path("id") id: String): Book

    @GET("/v2/book/{id}")
    fun bookCancelableRequest(@Path("id") id: String): CancelableRequest<Book>

    @GET("/mobile-api/q/train/getTodaySchedule")
    fun getTodaySchedule(@Query("stationId") stationId: String): CancelableRequest<ResponseData<List<Train>>>

    @FormUrlEncoded
    @POST("/v3/dynamic/getCurrentTrainMessage")
    fun getCurrentTrainMessageV3Cancelable(): CancelableRequest<ResponseData<CurrentTrainMessage>>

    class Book {
        var pubdate: String = ""
    }

    class ResponseData<T> {
        var success: Boolean = false
        var code: Int = 0
        var message: String = ""
        var data: T? = null
    }

    class Train {
        var trainNumber: String = ""
    }

    class CurrentTrainMessage {

    }
}