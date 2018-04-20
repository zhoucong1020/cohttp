package winsion.net.kotlinandroid.cohttp

import net.winsion.cohttp.CoHttpBuilder
import okhttp3.OkHttpClient

/**
 * Created by zhoucong on 2017/6/30.
 */
class CoHttp(
        private val baseUrl: String,
        private val client: OkHttpClient
) {
    @Suppress("UNCHECKED_CAST")
    fun <T> create(clazz: Class<T>): T {
        return Class.forName(clazz.name + "Impl").newInstance() as T
    }

    companion object {
        fun builder(): CoHttpBuilder {
            return CoHttpBuilder()
        }
    }
}