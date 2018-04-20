package net.winsion.cohttp

import net.winsion.cohttp.markers.*
import okhttp3.*
import java.lang.reflect.Method
import java.net.URLEncoder

class RequestBuilder(
        private val method: Method
) {
    private var requestMethod: String = ""
    private var url: String = ""

    private var pathParameter: MutableList<PathParameter> = ArrayList()
    private var queryParameter: MutableList<QueryParameter> = ArrayList()
    private var fieldParameter: MutableList<FieldParameter> = ArrayList()
    private var partParameter: MutableList<PartParameter> = ArrayList()

    private var isFormUrlEncoded: Boolean = false
    private var isMultipart: Boolean = false

    init {
        resolveBasic()
        resolveParameter()
    }

    fun build(baseUrl: String, args: Array<Any>?, converterFactories: List<ConverterFactory>): Request {
        val sb = StringBuilder()
        sb.append(baseUrl)
        sb.append(url)
        if (args != null) {
            pathParameter.forEach {
                val start = sb.indexOf(it.pattern)
                if (start > 0) {
                    sb.replace(start, start + it.pattern.length, encode(args[it.index], it.encoded))
                }
            }
            queryParameter.forEach {
                if (!it.isMap) {
                    addQuery(sb, it.name, encode(args[it.index], it.encoded))
                } else {
                    val arg = args[it.index]
                    if (arg is Map<*, *>) {
                        arg.forEach { key, value ->
                            addQuery(sb, key.toString(), encode(value!!, it.encoded))
                        }
                    }
                }
            }
        }

        return when (requestMethod) {
            "GET" -> {
                Request.Builder()
                        .url(sb.toString())
                        .build()
            }
            "POST" -> {
                Request.Builder()
                        .url(sb.toString())
                        .post(buildRequestBody(args))
                        .build()
            }
            "PUT" -> {
                Request.Builder()
                        .url(sb.toString())
                        .put(buildRequestBody(args))
                        .build()
            }
            "DELETE" -> {
                Request.Builder()
                        .url(sb.toString())
                        .delete(buildRequestBody(args))
                        .build()
            }
            "HEAD" -> {
                Request.Builder()
                        .url(sb.toString())
                        .head()
                        .build()
            }
            else -> {
                throw RuntimeException("request method not found")
            }
        }
    }

    private fun buildRequestBody(args: Array<Any>?): RequestBody {
        if (args != null) {
            if (isFormUrlEncoded) {
                val builder = FormBody.Builder()
                fieldParameter.forEach {
                    if (!it.isMap) {
                        builder.add(it.name, encode(args[it.index], it.encoded))
                    } else {
                        val arg = args[it.index]
                        if (arg is Map<*, *>) {
                            arg.forEach { key, value ->
                                builder.add(key.toString(), encode(value!!, it.encoded))
                            }
                        }
                    }
                }
                return builder.build()
            }
            if (isMultipart) {
                val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                partParameter.forEach {
                    if (!it.isMap) {
                        val arg = args[it.index]
                        if (arg is RequestBody) {
                            builder.addPart(
                                    Headers.of("Content-Disposition", "form-data; ${it.header}"),
                                    arg
                            )
                        }
                    } else {
                        val arg = args[it.index]
                        if (arg is Map<*, *>) {
                            arg.forEach { key, value ->
                                if (value is RequestBody) {
                                    builder.addPart(
                                            Headers.of("Content-Disposition", "form-data; $key"),
                                            value
                                    )
                                }
                            }
                        }
                    }
                }
                return builder.build()
            }
        }

        return FormBody.Builder().build()
    }

    private fun resolveBasic() {
        method.getAnnotation(GET::class.java)?.let {
            requestMethod = "GET"
            url = it.value
        }
        method.getAnnotation(POST::class.java)?.let {
            requestMethod = "POST"
            url = it.value
        }

        method.getAnnotation(FormUrlEncoded::class.java)?.let {
            isFormUrlEncoded = true
        }
        method.getAnnotation(Multipart::class.java)?.let {
            isMultipart = true
        }
    }

    private fun resolveParameter() {
        method.parameterAnnotations.indices
                .filter { method.parameterAnnotations[it].isNotEmpty() }
                .forEach {
                    method.parameterAnnotations[it].forEach { annotation ->
                        if (annotation is Path) {
                            val pattern = "{${annotation.value}}"
                            pathParameter.add(PathParameter(it, annotation.encoded, pattern))
                        }
                        if (annotation is Query) {
                            queryParameter.add(QueryParameter(it, annotation.encoded, annotation.value))
                        }
                        if (annotation is QueryMap) {
                            queryParameter.add(QueryParameter(it, annotation.encoded, "", true))
                        }
                        if (annotation is Field) {
                            fieldParameter.add(FieldParameter(it, annotation.encoded, annotation.value))
                        }
                        if (annotation is FieldMap) {
                            fieldParameter.add(FieldParameter(it, annotation.encoded, "", true))
                        }
                        if (annotation is Part) {
                            partParameter.add(PartParameter(it, annotation.encoding, annotation.value))
                        }
                        if (annotation is PartMap) {
                            partParameter.add(PartParameter(it, annotation.encoding, "", true))
                        }
                    }
                }
    }

    private fun encode(arg: Any, encoded: Boolean): String {
        if (!encoded) {
            return URLEncoder.encode(arg.toString(), "UTF-8")
        }
        return arg.toString()
        //TODO::support other type
    }

    private fun addQuery(url: StringBuilder, name: String, value: String) {
        if (url.indexOf("?") > 0) {
            url.append("&")
        } else {
            url.append("?")
        }
        url.append(name)
        url.append("=")
        url.append(value)
    }

    data class PathParameter(
            val index: Int,
            val encoded: Boolean,
            val pattern: String
    )

    data class QueryParameter(
            val index: Int,
            val encoded: Boolean,
            val name: String,
            val isMap: Boolean = false
    )

    data class FieldParameter(
            val index: Int,
            val encoded: Boolean,
            val name: String,
            val isMap: Boolean = false
    )

    data class PartParameter(
            val index: Int,
            val encoding: String,
            val header: String,
            val isMap: Boolean = false
    )
}