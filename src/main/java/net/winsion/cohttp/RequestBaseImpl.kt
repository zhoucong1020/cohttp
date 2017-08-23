package winsion.net.kotlinandroid.cohttp

import net.winsion.cohttp.Converter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.util.concurrent.Executors
import kotlin.coroutines.experimental.suspendCoroutine


/**
 * Created by zhoucong on 2017/7/3.
 */
class RequestBaseImpl(
        val client: OkHttpClient
) {
    private val io = Executors.newSingleThreadExecutor()

    suspend fun <T> coroutineRequest(request: Request, converter: Converter<ResponseBody, T>): T? {
        return suspendCoroutine { continuation ->
            io.submit {
                continuation.resume(converter.convert(request(request)))
            }
        }
    }

    private fun request(request: Request): ResponseBody {
        val response = client.newCall(request).execute()
        return response.body()!!
    }
}