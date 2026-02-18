package com.firoz.rafsan.ftptv

import android.R.attr.name
import android.R.attr.rating
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.regex.Pattern
import kotlin.text.contains

object FTPLib {
    private const val FMUrlRegex = """href=["']([^.].+?)["']"""
    private const val TPUrlRegex = """href=["'](/t.+?)["']>"""

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
        val matcher = Pattern.compile(if (isFM) FMUrlRegex else TPUrlRegex, Pattern.MULTILINE).matcher(data)

        while (matcher.find()) {
            itemList.add(FTPItem(baseUrl, matcher.group(1), isFM))
        }
        return itemList as List<FTPItem>
    }

    @JvmStatic
    fun getMetaData(item: FTPItem, name: String, isFM: Boolean): FTPMetadata {
        return if (isFM) {
            val res = try {
                sendGet(
                    "https://fmftp.net/api/search?search=${
                        URLEncoder.encode(
                            name, "UTF-8"
                        )
                    }"
                )
            } catch (_: Exception) {
                ""
            }

            val jsonArray = JSONArray(res)

            if (jsonArray.length() == 0) {
                // imdb goes here.
                imdb.getImdbMetadata(item.name)
            } else {

                var poster: String? = null
                var rating: String? = null
                var plot: String? = null

                for (i in 0 until jsonArray.length()){
                    try {
                        val jsonObject = jsonArray.getJSONObject(i)!!
                        val url = jsonObject.getString("url")
                        val itemURL = URLDecoder.decode(item.itemURL, "utf-8")
                        if (url !in itemURL) continue
                        plot = jsonObject.getString("overview")
                        poster = "http://fmftp.net/content-images/movies/posters${jsonObject.getString("poster_path")}"
                        rating = "${jsonObject.getDouble("online_rating")} / 10"

                        break

                    } catch (_: Exception){

                    }
                }
                if (poster != null && plot != null && rating != null)  FTPMetadata(poster,plot,rating)
                else imdb.getImdbMetadata(item.name)
            }

        } else {
            var res = try {
                sendPost(
                    "http://www.timepassbd.live/rpc.php",
                    mapOf("queryString" to name)
                )
            } catch (e: Exception) { ""}
            val ftpUrlRegex = Regex("""href=\"(.+?)\"""").find(res)
            if (ftpUrlRegex != null) {
                val pageUrl = "http://timepassbd.live/${ftpUrlRegex.groups[1]!!.value}"
                res = try {
                    sendGet(pageUrl)
                } catch (_: Exception) {""}
                val posterImageRegex =
                    Regex("""<img.+?src=\"(.+?)\" class=\".+?wp-post-image\"""").find(res)
                val plotRegex =
                    Regex("""<div .+?id=\"Details\">\n.+?\n.+?<p>(.+?)</p>""").find(res)
                val ratingRegex = Regex("""<td>\n\t+(\d{1,2}\.\d / 10)""").find(res)

                val imageURL = if (posterImageRegex != null) {
                    posterImageRegex.groups[1]!!.value
                } else {
                    ""
                }
                val plot = if (plotRegex != null) {
                    plotRegex.groups[1]!!.value
                } else {
                    ""
                }
                val rating = if (ratingRegex != null) {
                    ratingRegex.groups[1]!!.value
                } else {
                    "0 / 10"
                }

                FTPMetadata(imageURL, plot, rating)
            } else {
                // imdb work goes here.
                imdb.getImdbMetadata(item.name)
            }
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