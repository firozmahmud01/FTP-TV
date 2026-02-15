package com.firoz.rafsan.ftptv

import android.R.attr.name
import android.R.attr.rating
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.regex.Pattern

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
        return connection.getInputStream().readBytes().toString()
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
    fun getMetaData(item: FTPItem, isFM: Boolean): FTPMetadata {
        return if (isFM) {
            val res = try {
                sendGet(
                    "https://fmftp.net/api/search?search=${
                        URLEncoder.encode(
                            item.name, "UTF-8"
                        )
                    }"
                )
            } catch (_: Exception) {
                ""
            }

            val posterImageRegex = Regex("""\"poster_path\":\"(.+?)\"""").find(res)
            val plotRegex = Regex("""\"overview\":\"(.+?)\"""").find(res)
            val ratingRegex = Regex("""\"online_rating\":(.+?),\"""").find(res)
            val poster = if (posterImageRegex != null) {
                "http://fmftp.net/content-images/movies/posters${posterImageRegex.groups[1]!!.value}"
            } else {
                ""
            }
            val plot = if (plotRegex != null) {
                plotRegex.groups[1]!!.value
            } else {
                ""
            }
            val rating = if (ratingRegex != null) {
                "${ratingRegex.groups[1]!!.value} / 10"
            } else {
                "0 / 10"
            }
            FTPMetadata(poster, plot, rating)
        } else {
            var res = try {
                sendPost(
                    "http://www.timepassbd.live/rpc.php",
                    mapOf("queryString" to item.name.take(5))
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
                FTPMetadata("", "", "0 / 10")
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

        return connection.getInputStream().use { it.readBytes().toString() }
    }
}