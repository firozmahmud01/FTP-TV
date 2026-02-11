package com.firoz.rafsan.ftptv;

import java.util.ArrayList;

public class ftplib {
    public ftplib(){

    }

    private String sendGET(String url)
    {

    }

    private ArrayList<FTPItem> parseFTP(String data,boolean isFM){

    }
    public ArrayList<FTPItem> listDir(String path,boolean isFM){

    }





    public static class FTPItem{
        private String name,link;
        private boolean isDir;
        public FTPItem(String name,String link){
            this.name=name;
            this.link=link;
        }
        public String getName(){
            return name;
        }
        public String getLink(){
            return link;
        }
        public boolean isDir(){
            if(link.endsWith("/"))
                return true;
            else
                return false;
        }
    }




}
