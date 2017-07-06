package winsion.net.kotlinandroid.cohttp

import okhttp3.OkHttpClient

/**
 * Created by zhoucong on 2017/6/30.
 */
class CoHttpBuilder {
    private var baseUrl: String = ""
    private var client: OkHttpClient? = null

    fun baseUrl(baseUrl: String): CoHttpBuilder {
        this.baseUrl = baseUrl
        return this
    }

    fun client(client: OkHttpClient): CoHttpBuilder {
        this.client = client
        return this
    }

    fun build(): CoHttp {
        client?.let {
            client = OkHttpClient()
        }

        val coHttp = CoHttp(baseUrl, client!!)
        return coHttp
    }
}