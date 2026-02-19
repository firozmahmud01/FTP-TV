package com.firoz.rafsan.ftptv

import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.regex.Pattern

object FTPLib {
    private const val FM_URL_REGEX = """href=["']([^.].+?)["']"""
    private const val TP_URL_REGEX = """href=["'](/t.+?)["']>"""

    @JvmStatic
    @Throws(Exception::class)
    fun listDir(path: String, isFM: Boolean): List<FTPItem> {
        return parseFtp(path, sendGet(path), isFM)
    }

    @Throws(Exception::class)
    private fun sendGet(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        return String(connection.getInputStream().readBytes())
    }

    private fun parseFtp(baseUrl: String, data: String, isFM: Boolean): List<FTPItem> {
        val itemList = mutableListOf<FTPItem>()
        val matcher =
            Pattern.compile(if (isFM) FM_URL_REGEX else TP_URL_REGEX, Pattern.MULTILINE).matcher(data)

        while (matcher.find()) {
            itemList.add(FTPItem(baseUrl, matcher.group(1), isFM))
        }
        return itemList as List<FTPItem>
    }

    private fun getFMMetadata(item: FTPItem, name: String): FTPMetadata {

        val res = runCatching {
            sendGet("https://fmftp.net/api/search?search=${URLEncoder.encode(name, "UTF-8")}")
        }.getOrDefault("")

        if (res.isEmpty()) return imdb.getImdbMetadata(item.name)
        else {
            val jsonArray = JSONArray(res)

            if (jsonArray.length() == 0) {
                // imdb goes here.
                return imdb.getImdbMetadata(item.name)
            } else {
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = runCatching { jsonArray.getJSONObject(i) }.getOrElse {
                        continue
                    }
                    val itemURL = URLDecoder.decode(item.itemURL, "utf-8")
                    val url = runCatching { jsonObject.getString("url") }.getOrDefault("")
                    if (!itemURL.contains(url)) continue
                    val plot = runCatching { jsonObject.getString("overview") }.getOrDefault("")
                    val poster = runCatching {
                        "http://fmftp.net/content-images/movies/posters${
                            jsonObject.getString("poster_path")
                        }"
                    }.getOrDefault("")
                    val rating =
                        runCatching { "${jsonObject.getDouble("online_rating")} / 10" }.getOrDefault(
                            ""
                        )

                    return if (poster.isNotEmpty() && plot.isNotEmpty() && rating.isNotEmpty()) {
                        FTPMetadata(poster, plot, rating)
                    } else {
                        imdb.getImdbMetadata(item.name)
                    }
                }
            }
        }
        return FTPMetadata()
    }

    private fun getTPMetadata(item: FTPItem, name: String): FTPMetadata {
        var res = runCatching {
            sendPost(
                "http://www.timepassbd.live/rpc.php",
                mapOf("queryString" to name)
            )
        }.getOrDefault("")
        val ftpUrlRegex = Regex("""href="(.+?)"""").find(res)
        if (ftpUrlRegex != null) {
            val pageUrl = "http://timepassbd.live/${ftpUrlRegex.groups[1]?.value ?: ""}"
            res = runCatching { sendGet(pageUrl) }.getOrDefault("")
            if (res.isEmpty()) return imdb.getImdbMetadata(item.name)
            val posterImageRegex = Regex("""<img.+?src="(.+?)" class=".+?wp-post-image"""").find(res)
            val plotRegex = Regex("""<div .+?id="Details">\n.+?\n.+?<p>(.+?)</p>""").find(res)
            val ratingRegex = Regex("""<td>\n\t+(\d{1,2}\.\d / 10)""").find(res)

            val imageURL = posterImageRegex?.groups[1]?.value ?: ""
            val plot = plotRegex?.groups[1]?.value ?: ""
            val rating =  ratingRegex?.groups[1]?.value ?: "0 / 10"

            return FTPMetadata(imageURL, plot, rating)
        } else {
            // imdb work goes here.
            return imdb.getImdbMetadata(item.name)
        }
    }

    @JvmStatic
    fun getMetaData(item: FTPItem, name: String, isFM: Boolean): FTPMetadata {
        return if (isFM) {
            getFMMetadata(item, name)
        } else {
            getTPMetadata(item, name)
        }
    }

    @Throws(Exception::class)
    private fun sendPost(url: String, body: Map<String, String>): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        val body = body.map { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }.joinToString("&").toByteArray()
        connection.apply {
            requestMethod = "POST"
            doOutput = true
            setFixedLengthStreamingMode(body.size)
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            setRequestProperty("Content-Length", "${body.size}")
        }

        connection.getOutputStream().use { it.write(body) }

        return connection.getInputStream().use { String(it.readBytes()) }
    }
}