package com.firoz.rafsan.ftptv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class imdb {
    private static String encodeData(String data) throws Exception{
        return URLEncoder.encode(data, StandardCharsets.UTF_8.toString());
    }

    public static FTPMetadata getImdbMetadata(String title) throws Exception{
        String data=sendGET("http://www.omdbapi.com/?apikey=64619439&t="+encodeData(title));
        return new FTPMetadata("", "", "");
    }
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
}
