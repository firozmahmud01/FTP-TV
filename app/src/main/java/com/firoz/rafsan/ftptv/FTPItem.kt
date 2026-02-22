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
    var isSeries: Boolean = false


    init {
        val x = capturedUrl.split("/")
        isDir = capturedUrl.endsWith("/")
        name = URLDecoder.decode(if (isDir) x.takeLast(2).first() else getMatchedname(x))
        itemURL = if (isFM) "$baseUrl$capturedUrl" else "http://ftp.timepassbd.live$capturedUrl"

    }


    fun onlyChar(name:String):String{
        var output="";
        for(i in 0 until name.length){
            if(name[i].isLetter()||name[i].isDigit()||name[i]==' '){
                output+=name[i];
            }
        }
        return output;
    }


    fun getMatchedname(url: List<String>):String{
        val name=onlyChar(url.last());
        val all=url.takeLast(3)
        var output="";
        val match1=onlyChar(all.first());
        val match2=onlyChar(all[1]);
        for(i in 0 until match1.length){
            if(match1[i]==name[i]){
                output+=match1[i];
            }else{
                break
            }
        }
        if(output.length>=3){
            return all.first()
        }
        output="";
        for(i in 0 until match2.length){
            if(name[i]==match2[i]){
                output+=match2[i];
            }else{
                break
            }
        }
        if(output.length>=3){
            isSeries=true;
            return all[1];
        }
        return name;
    }

}
