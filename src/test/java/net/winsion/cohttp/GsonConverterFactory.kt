package net.winsion.cohttp

import com.google.gson.Gson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

class GsonConverterFactory : ConverterFactory {
    val gson: Gson = Gson()

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>): Converter<ResponseBody, *>? {
        return object : Converter<ResponseBody, Any> {
            override fun convert(value: ResponseBody): Any {
                return gson.fromJson(value.string(), type)
            }
        }
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>): Converter<*, RequestBody>? {
        return null
    }
}