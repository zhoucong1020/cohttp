package net.winsion.cohttp

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import net.winsion.cohttp.support.TestGetInterface
import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockserver.client.server.MockServerClient
import org.mockserver.junit.MockServerRule
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse

class TestCache {
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
    }

    @Test
    fun test() {
        val cohttp = CoHttp.builder()
                .baseUrl("http://localhost:5000")
                .client(OkHttpClient())
                .build()

        launch(CommonPool) {
            val testGetInterface = cohttp.create(TestGetInterface::class.java)

            Assert.assertEquals(expected, testGetInterface.get())
            Assert.assertEquals(expected, testGetInterface.get())
            Assert.assertEquals(expected, testGetInterface.get())
            Assert.assertEquals(expected, testGetInterface.get())
        }

        Thread.sleep(1000)
    }
}