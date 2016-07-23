package com.mithil.popular;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Mithil on 11/11/2015.
 */
public class MovieHttpClient {
    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final String API_KEY = "?api_key=6a3a21fe65278b5f76b14bd899a91f7b";

    public String getData(String flag, String page){

        StringBuilder url = new StringBuilder();
        HttpURLConnection con = null;
        InputStream is = null;
        if(flag.equals("2")){
            url.append(BASE_URL).append("tv");
        }
        else{
            url.append(BASE_URL).append("movie");
        }
        url.append("/popular" + API_KEY + "&page=").append(page);

        try {
            con = (HttpURLConnection)(new URL(url.toString())).openConnection();
            con.setRequestMethod("GET");
            con.connect();

            StringBuilder buffer = new StringBuilder();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line ;
            while (  (line = br.readLine()) != null )
                buffer.append(line).append("\n");

            return buffer.toString();

        } catch (Throwable e) {
            e.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;
    }
}
