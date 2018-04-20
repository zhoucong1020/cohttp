package net.winsion.cohttp

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test

class TestCache {
    @Test
    fun test() {
        val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .build()

        val cohttp = CoHttp.builder()
                .baseUrl("https://api.douban.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            val book = cohttp.create(API::class.java).book("1220562")
            println(book.pubdate)
        }

        Thread.sleep(5000)
    }

    @Test
    fun testCancelableRequest() {
        val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .build()

        val cohttp = CoHttp.builder()
                .baseUrl("https://api.douban.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            val request = cohttp.create(API::class.java).bookCancelableRequest("1220562")
            Thread(Runnable {
                Thread.sleep(10)
                request.cancel()
            }).start()
            try {
                val book = request.call()
                println(book)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        Thread.sleep(5000)
    }

    @Test
    fun testException() {
        val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .build()

        val cohttp = CoHttp.builder()
                .baseUrl("https://api.douba123123n.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            try {
                val book = cohttp.create(API::class.java).book("1220562")
                println(book.pubdate)
            } catch (e: Exception) {
                println(e.message)
            }
        }

        Thread.sleep(5000)
    }

    @Test
    fun testResponseData() {
        val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .build()

        val cohttp = CoHttp.builder()
                .baseUrl("http://mobile-api.tlbl.winsion.net")
                .client(client)
                .addConverterFactory(GsonConverterFactory())
                .build()

        launch(CommonPool) {
            val request = cohttp.create(API::class.java).getCurrentTrainMessageV3Cancelable()
            val result = request.call()
            println(result!!.success)
            println(result!!.code)
            println(result!!.message)
        }

        Thread.sleep(5000)
    }


}