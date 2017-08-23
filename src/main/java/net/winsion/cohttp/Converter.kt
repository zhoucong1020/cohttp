package net.winsion.cohttp

import java.io.IOException



interface Converter<in F, out T> {
    @Throws(IOException::class)
    fun convert(value: F): T
}