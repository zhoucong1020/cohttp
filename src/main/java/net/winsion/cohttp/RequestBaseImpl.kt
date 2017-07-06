package winsion.net.kotlinandroid.cohttp

import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.Executors
import kotlin.coroutines.experimental.suspendCoroutine

/**
 * Created by zhoucong on 2017/7/3.
 */
open class RequestBaseImpl {
    private val io = Executors.newSingleThreadExecutor()
    private val client = OkHttpClient()

    suspend fun coroutineWebRequest(url: String): String {
        return suspendCoroutine {
            continuation ->
            io.submit {
                continuation.resume(webRequest(url))
            }
        }
    }

    private fun webRequest(url: String): String {
        val request = Request.Builder()
                .url(url)
                .build()

        val response = client.newCall(request).execute()
        return response.body()!!.string()
    }
}