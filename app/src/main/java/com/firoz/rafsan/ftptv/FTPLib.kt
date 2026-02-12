package com.firoz.rafsan.ftptv

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

object FTPLib {
    private const val FMUrlRegex = "href=[\"']([^.].+)[\"']"
    private const val TPUrlRegex = "href=[\"'](/t.+?)[\"']>"

    @JvmStatic
    @Throws(Exception::class)
    fun listDir(path: String?, isFM: Boolean): List<FTPItem> {
        return parseFtp(sendGet(path), isFM)
    }

    @Throws(Exception::class)
    private fun sendGet(url: String?): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        val inputStream = connection.getInputStream()
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        for(line in bufferedReader.lines()) { stringBuilder.append(line) }

        return stringBuilder.toString()
    }

    @JvmStatic
    fun o(): String {
        return Test.x()
    }

    private fun parseFtp(data: String, isFM: Boolean): List<FTPItem> {
        val itemList = mutableListOf<FTPItem>()
        val matcher = Pattern.compile(if (isFM) FMUrlRegex else TPUrlRegex).matcher(data)

        while (matcher.find()) {
            itemList.add(FTPItem(matcher.group(1)))
        }
        return itemList as List<FTPItem>
    }
}