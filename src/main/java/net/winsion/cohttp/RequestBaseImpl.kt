package net.winsion.cohttp

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.IOException
import java.util.concurrent.Executors
import kotlin.coroutines.experimental.suspendCoroutine


/**
 * Created by zhoucong on 2017/7/3.
 */
class RequestBaseImpl(
        private val client: OkHttpClient
) {
    private val io = Executors.newCachedThreadPool()

    suspend fun <T> coroutineRequest(request: Request, converter: Converter<ResponseBody, T>): T? {
        val call = buildCall(request)
        return coroutineRequest(call, converter)
    }

    suspend fun <T> coroutineRequest(call: Call, converter: Converter<ResponseBody, T>): T? {
        return suspendCoroutine { continuation ->
            io.submit {
                try {
                    val result = converter.convert(request(call))
                    continuation.resume(result)
                } catch (throwable: Throwable) {
                    if (throwable is IOException && throwable.message == "Canceled") {
                        //ignore
                    } else {
                        continuation.resumeWithException(throwable)
                    }
                }
            }
        }
    }

    fun buildCall(request: Request): Call {
        return client.newCall(request)
    }

    private fun request(call: Call): ResponseBody {
        val response = call.execute()
        return response.body()!!
    }
}