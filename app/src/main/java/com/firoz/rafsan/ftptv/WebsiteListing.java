package com.firoz.rafsan.ftptv;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class WebsiteListing {
    private static String sendGET(String urllink) throws Exception{
        URL url = new URL(urllink);
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();

        try {
            // Open the connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // Optional: Set a connection timeout (milliseconds)
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();


            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response from the input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {
                throw new Exception("Failed to send get request in imdb");
            }
        } finally {
            // Ensure the connection is always closed
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString();
    }

    private static ArrayList<MovieEntity> fmMovieList() throws Exception {
        String ftpList=sendGET("https://fmftp.net/api/movies?limit=500000");
        JSONObject jo=new JSONObject(ftpList);
        ArrayList<MovieEntity> list=new ArrayList<>();
        JSONArray jlist=jo.getJSONArray("data");
        for(int i=0;i<jlist.length();i++){
            JSONObject movie=jlist.getJSONObject(i);
            MovieEntity entity=new MovieEntity();
            entity.name=movie.getString("title");
            entity.itemURL="https://fmftp.net"+movie.getString("url");
            entity.websiteid=""+movie.getLong("id");
            entity.category=movie.getJSONObject("Library").getString("name");
            entity.upload_date=movie.getString("updatedAt");
            entity.genre=movie.getString("genre");
            entity.imdbid=movie.getString("imdb_id");
            entity.imageURL="https://fmftp.net/content-images/movies/posters"+movie.getString("poster_path");
            entity.plot=movie.getString("overview");
            entity.rating=movie.getString("online_rating");
            list.add(entity);
        }
        return list;
    }

    private static ArrayList<MovieEntity> fmSeriesList() throws Exception {
        String ftpList=sendGET("https://fmftp.net/api/tv-shows?limit=500000");
        JSONObject jo=new JSONObject(ftpList);
        ArrayList<MovieEntity> list=new ArrayList<>();
        JSONArray jlist=jo.getJSONArray("data");
        for(int i=0;i<jlist.length();i++){
            JSONObject movie=jlist.getJSONObject(i);
            MovieEntity entity=new MovieEntity();
            entity.name=movie.getString("title");
            entity.itemURL="https://fmftp.net"+movie.getString("url");
            entity.websiteid=""+movie.getLong("id");
            entity.category=movie.getJSONObject("Library").getString("name");
            entity.upload_date=movie.getString("updatedAt");
            entity.genre=movie.getString("genre");
            entity.imdbid=movie.getString("imdb_id");
            entity.imageURL="https://fmftp.net/content-images/movies/posters"+movie.getString("poster_path");
            entity.plot=movie.getString("overview");
            entity.rating=movie.getString("online_rating");
            list.add(entity);
        }
        return list;
    }

    private static ArrayList<MovieEntity> tpMovieList() throws Exception {
        Pattern linkpattern=Pattern.compile("movie");
        String ftpList=sendGET("http://timepassbd.live/allmovies.php?page=1&entries=5&sort=DESC&w=grid");
        int postindex=ftpList.indexOf("<!--/ Posts -->")+"<!--/ Posts -->".length();
        String[] moviesPart=(ftpList.substring(postindex, ftpList.indexOf("class=\"pagination\"",postindex))).split("\"col-lg-3 col-md-4 col-sm-6 col-xs1-8 col-xs-12\"");
        for(int i=1;i<moviesPart.length;i++){
            try {
                String link = moviesPart[i];
                String linkonly = linkpattern.matcher(link).group();
                int a = 10;
            }catch (Exception e){
                int a=100;
            }
        }

        return new ArrayList<>();
    }

    private static ArrayList<MovieEntity> tpSeriesList() throws Exception {
        return null;
    }


    public static ArrayList<MovieEntity> movieList() throws Exception {

        return tpMovieList();
    }
}
