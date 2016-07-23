package com.mithil.popular;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mithil on 11/11/2015.
 */
public class FetchMovieTask extends AsyncTask<String, Object, List<Movie>> {

    public  MovieAdapter adapter;
    public  FetchMovieTask(MovieAdapter adapter){
        this.adapter = adapter;
    }
    protected List<Movie> doInBackground(String... params) {
        List<Movie> movieList = new ArrayList<>();
        for(int i = 1 ; i<=3; i++) {
            String data = new MovieHttpClient().getData(params[0],Integer.toString(i));
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int j = 0;j<jsonArray.length();j++ ){
                    Movie m = new Movie();
                    JSONObject movie = jsonArray.getJSONObject(j);
                    if (params[0].equals("2"))
                        m.name=getString("original_name",movie);
                    else
                        m.name = getString("original_title", movie);
                    m.image = getString("poster_path", movie);
                    m.overview = getString("overview", movie);
                    m.rating = getString("vote_average", movie);
                    if (params[0].equals("2")){
                        m.date = "First Air Date: " + getString("first_air_date", movie);
                    }
                    else{
                        m.date = "Release Date: " + getString("release_date", movie);
                    }
                    m.id = ""+ getInt("id",movie);
                    m.image2 = getString("backdrop_path", movie);
                    movieList.add(m);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return movieList;
    }

    @Override
    protected void onPostExecute(List<Movie> movieList) {
        if (movieList!=null){
            adapter.clear();
            for (Movie m : movieList)
                adapter.add(m);
        }
    }

    private static JSONObject getObject(String tagName, JSONObject jObj)  throws JSONException {
        return jObj.getJSONObject(tagName);
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float  getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int  getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}
