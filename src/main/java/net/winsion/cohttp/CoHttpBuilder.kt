package net.winsion.cohttp

import okhttp3.OkHttpClient

/**
 * Created by zhoucong on 2017/6/30.
 */
class CoHttpBuilder {
    private var baseUrl: String = ""
    private var client: OkHttpClient? = null
    private var converterFactories: MutableList<ConverterFactory> = ArrayList()

    init {
        converterFactories.add(ResponseBodyConverterFactory())
        converterFactories.add(StringConverterFactory())
    }

    fun baseUrl(baseUrl: String): CoHttpBuilder {
        this.baseUrl = baseUrl
        return this
    }

    fun client(client: OkHttpClient): CoHttpBuilder {
        this.client = client
        return this
    }

    fun addConverterFactory(converterFactory: ConverterFactory): CoHttpBuilder {
        converterFactories.add(converterFactory)
        return this
    }

    fun build(): CoHttp {
        if (client == null) {
            client = OkHttpClient()
        }

        return CoHttp(baseUrl, client!!, converterFactories)
    }
}