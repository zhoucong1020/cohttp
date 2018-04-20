package net.winsion.cohttp

import okhttp3.Call
import okhttp3.Request
import okhttp3.ResponseBody

class CancelableRequest<T>(
        private val request: Request,
        private val converter: Converter<ResponseBody, T>,
        private val base: RequestBaseImpl
) {
    private var call: Call? = null

    suspend fun call(): T? {
        call = base.buildCall(request)
        return base.coroutineRequest(call!!, converter)
    }

    fun cancel() {
        if (call != null) {
            call!!.cancel()
        } else {
            throw RuntimeException("can not cancel request that not start")
        }
    }
}