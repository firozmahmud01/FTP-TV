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
        private String name,link,image,rating;
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
        public String getImage(){
            return image;
        }
        public String getRating(){
            return rating;
        }
        public void setImage(String image){
            this.image=image;
        }
        public void setRating(String rating){
            this.rating=rating;
        }

        public boolean isDir(){
            if(link.endsWith("/"))
                return true;
            else
                return false;
        }
    }




}
