package net.winsion.cohttp

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockserver.client.server.MockServerClient
import org.mockserver.junit.MockServerRule
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import java.io.File
import java.util.*


class TestPost {
    @get:Rule
    var server = MockServerRule(this, 5000)

    val expected = "{ message: 'ok' }"

    @Before
    fun initMock() {
        val mockClient = MockServerClient("localhost", 5000)

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/postWithFieldResponseString")
                        .withBody("q=test")
                        .withMethod("POST")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/postWithFieldResponseResponseBody")
                        .withBody("q=test")
                        .withMethod("POST")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/postWithFieldMap")
                        .withBody("q1=test&q2=test")
                        .withMethod("POST")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )

        mockClient.`when`(
                HttpRequest.request()
                        .withPath("/postWithMultipart")
                        .withMethod("POST")
        ).respond(
                HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(expected)
        )
    }

    @Test
    fun testPostWithFieldResponseString() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            Assert.assertEquals(expected, cohttp.create(TestPostInterface::class.java).postWithFieldResponseString("test"))
        }

        Thread.sleep(1000)
    }

    @Test
    fun testPostWithFieldResponseResponseBody() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            Assert.assertEquals(expected, cohttp.create(TestPostInterface::class.java).postWithFieldResponseResponseBody("test").string())
        }

        Thread.sleep(1000)
    }

    @Test
    fun testPostWithFieldMap() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            Assert.assertEquals(expected, cohttp.create(TestPostInterface::class.java).postWithFieldMap(HashMap<String, String>().apply {
                put("q1", "test")
                put("q2", "test")
            }))
        }

        Thread.sleep(1000)
    }

    @Test
    fun testPostWithPart() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://172.16.5.34:9014")
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            val fileRequestBody = RequestBody.create(MediaType.parse("image/png"), File("hhht_01.jpg"))

            Assert.assertTrue(cohttp.create(TestPostInterface::class.java).postWithPart(fileRequestBody).contains("\"success\":true"))
        }

        Thread.sleep(2000)
    }

    @Test
    fun testPostWithPartMap() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://172.16.5.34:9014")
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            val fileRequestBody = RequestBody.create(MediaType.parse("image/png"), File("hhht_01.jpg"))
            val map = HashMap<String, RequestBody>().apply {
                put("name=\"file\";filename=\"hhht_01.jpg\"", fileRequestBody)
            }

            Assert.assertTrue(cohttp.create(TestPostInterface::class.java).postWithPartMap(map).contains("\"success\":true"))
        }

        Thread.sleep(2000)
    }
}