package net.winsion.cohttp

import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

interface ConverterFactory {
    fun responseBodyConverter(type: Type, annotations: Array<Annotation>): Converter<ResponseBody, *>?

    fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>): Converter<*, RequestBody>?
}

class ResponseBodyConverterFactory : ConverterFactory {
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>): Converter<ResponseBody, *>? {
        if (type == ResponseBody::class.java) {
            return object : Converter<ResponseBody, ResponseBody> {
                override fun convert(value: ResponseBody): ResponseBody {
                    return value
                }
            }
        }
        return null
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>): Converter<*, RequestBody>? {
        return null
    }
}

class StringConverterFactory : ConverterFactory {
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>): Converter<ResponseBody, *>? {
        if (type == String::class.java) {
            return object : Converter<ResponseBody, String> {
                override fun convert(value: ResponseBody): String {
                    return value.string()
                }
            }
        }
        return null
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>, methodAnnotations: Array<Annotation>): Converter<*, RequestBody>? {
        return null
    }
}