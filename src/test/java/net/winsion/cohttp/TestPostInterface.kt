package net.winsion.cohttp

import net.winsion.cohttp.markers.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import winsion.net.kotlinandroid.cohttp.markers.Query

interface TestPostInterface {
    @FormUrlEncoded
    @POST("/postWithFieldResponseString")
    suspend fun postWithFieldResponseString(
            @Field("q") q: String
    ): String

    @FormUrlEncoded
    @POST("/postWithFieldResponseResponseBody")
    suspend fun postWithFieldResponseResponseBody(
            @Field("q") q: String
    ): ResponseBody

    @FormUrlEncoded
    @POST("/postWithFieldMap")
    suspend fun postWithFieldMap(
            @FieldMap options: Map<String, String>
    ): String

    @Multipart
    @POST("/upload/uploadAttach")
    suspend fun postWithPart(
            @Part("name=\"file\";filename=\"file.jpg\"") file: RequestBody
    ): String

    @Multipart
    @POST("/upload/uploadAttach")
    suspend fun postWithPartMap(
            @PartMap options: Map<String, RequestBody>
    ): String
}