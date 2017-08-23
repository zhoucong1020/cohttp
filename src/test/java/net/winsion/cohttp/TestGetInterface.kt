package net.winsion.cohttp

import net.winsion.cohttp.markers.Path
import net.winsion.cohttp.markers.QueryMap
import net.winsion.cohttp.markers.GET
import net.winsion.cohttp.markers.Query

interface TestGetInterface {
    @GET("/get")
    suspend fun get(): String

    @GET("/getWithQuery")
    suspend fun getWithQuery(
            @Query("q") q: String
    ): String

    @GET("/getWithDefaultQueryAndQuery?sort=asc")
    suspend fun getWithDefaultQueryAndQuery(
            @Query("q") q: String
    ): String

    @GET("/getWithPathAndQuery/{path}")
    suspend fun getWithPathAndQuery(
            @Path("path") path: String,
            @Query("q") q: String
    ): String

    @GET("/getWithPathAndQueryMap/{path}")
    suspend fun getWithPathAndQueryMap(
            @Path("path") type: String,
            @QueryMap options: Map<String, String>
    ): String

    @GET("/getWithQueryResponseMockObject")
    suspend fun getWithQueryResponseMockObject(
            @Query("q") key: String
    ): MockObject

    class MockObject(
            var message: String = ""
    )
}