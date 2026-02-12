package com.firoz.rafsan.ftptv

import android.util.Xml
import java.net.URLDecoder
import java.nio.charset.CharsetDecoder

class FTPItem(link: String) {
    val isDir: Boolean = link.endsWith("/")
    private val x = link.split("/")
    val name: String = URLDecoder.decode(if (isDir) x.takeLast(2).first() else x.last())
}
