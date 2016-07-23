package com.mithil.popular;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String PREFS = "myPrefs";
    public Movie movie;
    public Button button;
    boolean reviewFlag;
    YouTubePlayer player;
    private String review;
    ArrayList<String> videoKeys;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    ArrayList<Movie> similarList;
    View rootView;
    int[] similarViewId = {R.id.similar1, R.id.similar2, R.id.similar3};

    public DetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_details, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }

        MenuItem fav = menu.findItem(R.id.fav);
        if (MainActivity.favMovies.contains(movie.id)) {
            fav.setIcon(R.drawable.ic_favorite_white_24dp);
            movie.isFav = 1;
        }

    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, movie.name + "\n\n" + "Plot : " + movie.overview);
        return shareIntent;
    }

    private void updateFav(MenuItem fav) {
        if (movie.isFav == 0) {
            fav.setIcon(R.drawable.ic_favorite_white_24dp);
            movie.isFav = 1;
            MainActivity.favMovies.add(movie.id);
            movie.save(sharedPreferences);
            Snackbar.make(Details.coordinatorLayout, "Added to favorites", Snackbar.LENGTH_SHORT).show();
        } else {
            fav.setIcon(R.drawable.ic_favorite_border_white_24dp);
            MainActivity.favMovies.remove(movie.id);
            movie.isFav = 0;
            Snackbar.make(Details.coordinatorLayout, "Removed from favorites", Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case R.id.fav:
                updateFav(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_details, container, false);
        Intent intent = getActivity().getIntent();
        movie = (Movie) intent.getParcelableExtra("movie");
        sharedPreferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        reviewFlag = false;
        similarList = new ArrayList<>();
        final TextView plot = (TextView) rootView.findViewById(R.id.overview);
        plot.setText(movie.overview);
        getActivity().setTitle(movie.name);
        final TextView rating = (TextView) rootView.findViewById(R.id.rating);
        rating.setText("Ratings: " + movie.rating);
        TextView date = (TextView) rootView.findViewById(R.id.date);
        date.setText(movie.date);

        loadTrailer();

        if (movie.date.contains("Release")) {
            FetchDetailsTask task = new FetchDetailsTask();
            task.execute("reviews");
        }

        FetchDetailsTask similarTask = new FetchDetailsTask();
        similarTask.execute("similar");


        return rootView;
    }

    private void loadReview() {
        button = (Button) rootView.findViewById(R.id.reviewButton);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review != null) {
                    View view = rootView.findViewById(R.id.reviewLayout);
                    if (!reviewFlag) {
                        view.setVisibility(View.VISIBLE);
                        button.setText("Hide");
                        TextView t = (TextView) rootView.findViewById(R.id.review);
                        t.setText(review);
                        reviewFlag = true;
                    } else {
                        reviewFlag = false;
                        view.setVisibility(View.GONE);
                        button.setText("Show Review");
                    }
                } else
                    Toast.makeText(getActivity().getApplicationContext(), "Review Unavailable!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTrailer() {
        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

        youTubePlayerFragment.initialize(getString(R.string.YOUTUBE_API), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider arg0, YouTubePlayer youTubePlayer, boolean restored) {
                player = youTubePlayer;
                if (!restored) {
                    FetchDetailsTask video = new FetchDetailsTask();
                    video.execute("videos");
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
                }

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult errorReason) {
                if (errorReason.isUserRecoverableError()) {
                    errorReason.getErrorDialog(getActivity(), 1).show();
                } else {
                    String error = String.format(getString(R.string.player_error), errorReason.toString());
                    Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadSimilar() {
        for (int i = 0; similarList != null && i < similarList.size(); i++) {
            ImageView imageView = (ImageView) rootView.findViewById(similarViewId[i]);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185" + similarList.get(i).image)
                    .into(imageView);
        }
        if (similarList != null)
            similarList.clear();
    }

    private void loadImage2() {
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image2);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w342" + movie.image2)
                .into(imageView);
        imageView.setVisibility(View.VISIBLE);
        FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.youtube_fragment);
        frameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        editor = sharedPreferences.edit();
        editor.putStringSet("favMovies", new HashSet<String>(MainActivity.favMovies));
        editor.apply();
        super.onPause();
    }

    class FetchDetailsTask extends AsyncTask<String, Void, String[]> {
        MovieAdapter adapter;

        FetchDetailsTask() {
        }

        private String fetchDetail(String detail) {
            String show = "";
            if (movie.date.contains("Release"))
                show = "movie";
            else
                show = "tv";
            String url = "http://api.themoviedb.org/3/" + show + "/" + movie.id + "/" + detail + "?api_key=" + getString(R.string.TMBD_API);
            HttpURLConnection con = null;
            InputStream is = null;
            StringBuilder buffer = new StringBuilder();
            try {
                con = (HttpURLConnection) (new URL(url).openConnection());
                con.setRequestMethod("GET");
                con.connect();

                is = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();


            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Throwable t) {
                }
                try {
                    con.disconnect();
                } catch (Throwable t) {
                }
            }

            return null;

        }

        @Override
        protected String[] doInBackground(String... params) {
            String data = fetchDetail(params[0]);
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                JSONObject detail = null;
                switch (params[0]) {
                    case "reviews":
                        detail = jsonArray.getJSONObject(0);
                        review = detail.getString("content");
                        break;
                    case "videos":
                        videoKeys = new ArrayList<>();
                        for (int i = jsonArray.length() - 1; i >= 0; i--) {
                            detail = jsonArray.getJSONObject(i);
                            videoKeys.add(detail.getString("key"));
                        }
                        break;
                    case "similar":
                        for (int i = 0; i < 3; i++) {
                            detail = jsonArray.getJSONObject(i);
                            Movie m = new Movie();
                            m.id = String.valueOf(detail.getInt("id"));
                            m.image = detail.getString("poster_path");
                            similarList.add(m);
                        }
                        break;
                }

            } catch (Throwable e) {
                e.printStackTrace();
            }
            return params;

        }

        @Override
        protected void onPostExecute(String[] string) {
            switch (string[0]) {
                case "reviews":
                    if(review != null)
                        loadReview();
                    break;
                case "videos":
                    if (videoKeys != null && videoKeys.size() != 0)
                        player.cueVideos(videoKeys);
                    else
                        loadImage2();
                    break;
                case "similar":
                    if (similarList != null && similarList.size() != 0)
                        loadSimilar();
                    else {
                        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.similar);
                        linearLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }
}
