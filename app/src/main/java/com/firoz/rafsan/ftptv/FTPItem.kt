package com.firoz.rafsan.ftptv

import java.net.URLDecoder

class FTPItem(
    baseUrl: String,
    capturedUrl: String,
    isFM: Boolean,
) {

    val name: String
    val isDir: Boolean
    val itemURL: String

    init {
        val x = capturedUrl.split("/")
        isDir = capturedUrl.endsWith("/")
        name = URLDecoder.decode(if (isDir) x.takeLast(2).first() else x.last())
        itemURL = if (isFM) "$baseUrl$capturedUrl" else "http://ftp.timepassbd.live$capturedUrl"
    }

}
