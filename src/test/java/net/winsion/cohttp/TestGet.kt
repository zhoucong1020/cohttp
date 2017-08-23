package net.winsion.cohttp

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockserver.client.server.MockServerClient
import org.mockserver.junit.MockServerRule
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.Parameter

class TestGet {
    @get:Rule
    var server = MockServerRule(this, 5000)

    val expected = "{ message: 'ok' }"

    @Before
    fun initMock() {
        val mockClient = MockServerClient("localhost", 5000)

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/get")
                        .withMethod("GET")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/getWithQuery")
                        .withQueryStringParameters(
                                Parameter("q", "test")
                        )
                        .withMethod("GET")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/getWithDefaultQueryAndQuery")
                        .withQueryStringParameters(
                                Parameter("sort", "asc"),
                                Parameter("q", "test")
                        )
                        .withMethod("GET")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/getWithPathAndQuery/test")
                        .withQueryStringParameters(
                                Parameter("q", "test")
                        )
                        .withMethod("GET")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/getWithPathAndQueryMap/test")
                        .withQueryStringParameters(
                                Parameter("q1", "test"),
                                Parameter("q2", "test")
                        )
                        .withMethod("GET")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/getWithQueryResponseMockObject")
                        .withQueryStringParameters(
                                Parameter("q", "test")
                        )
                        .withMethod("GET")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )
    }

    @Test
    fun testGet() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .build()

        launch(CommonPool) {
            Assert.assertEquals(expected, cohttp.create(TestGetInterface::class.java).get())
        }

        Thread.sleep(1000)
    }

    @Test
    fun testGetWithQuery() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .build()

        launch(CommonPool) {
            Assert.assertEquals(expected, cohttp.create(TestGetInterface::class.java).getWithQuery("test"))
        }

        Thread.sleep(1000)
    }

    @Test
    fun testGetWithDefaultQueryAndQuery() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .build()

        launch(CommonPool) {
            Assert.assertEquals(expected, cohttp.create(TestGetInterface::class.java).getWithDefaultQueryAndQuery("test"))
        }

        Thread.sleep(1000)
    }

    @Test
    fun testGetWithPathAndQuery() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .build()

        launch(CommonPool) {
            Assert.assertEquals(expected, cohttp.create(TestGetInterface::class.java).getWithPathAndQuery("test", "test"))
        }

        Thread.sleep(1000)
    }

    @Test
    fun testGetWithPathAndQueryMap() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .build()

        launch(CommonPool) {
            Assert.assertEquals(
                    expected,
                    cohttp.create(TestGetInterface::class.java).getWithPathAndQueryMap("test", HashMap<String, String>().apply {
                        put("q1", "test")
                        put("q2", "test")
                    })
            )
        }

        Thread.sleep(1000)
    }

    @Test
    fun testGetWithQueryResponseObject() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            val mockObject = cohttp.create(TestGetInterface::class.java).getWithQueryResponseMockObject("test")
            Assert.assertEquals("ok", mockObject.message)
        }

        Thread.sleep(1000)
    }
}