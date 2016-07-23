package com.mithil.popular;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Created by Mithil on 11/12/2015.
 */
public class MovieGridFragment extends android.support.v4.app.Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    public MovieAdapter movieAdapter;
    public static MovieAdapter favAdapter;
    public ArrayList<Movie> movies;
    View rootView;
    GridView gridView;
    Parcelable state;
    int PAGES = 3;
    SharedPreferences sharedPreferences;
    public static final String PREFS = "myPrefs";



    public static MovieGridFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MovieGridFragment fragment = new MovieGridFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        if(savedInstanceState==null || !savedInstanceState.containsKey("movies")){
            movies = new ArrayList<>();
        }
        else
            movies = savedInstanceState.getParcelableArrayList("movies");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.movieGridview);
        sharedPreferences = getActivity().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (mPage ==3)
            updateFav();
        else
            updateList();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movies);
        super.onSaveInstanceState(outState);
    }

    private void updateFav(){
        favAdapter = new MovieAdapter(getActivity(),new ArrayList<Movie>());
        for (String id : MainActivity.favMovies) {
            Movie m = new Movie();
            m.getSaved(sharedPreferences, id);
            favAdapter.add(m);
        }
        gridView.setAdapter(favAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = favAdapter.getItem(position);
                Intent i = new Intent(getActivity().getApplicationContext(), Details.class);
                i.putExtra("movie", movie);
                startActivity(i);
            }
        });
    }

    private void updateList() {
        movieAdapter = new MovieAdapter(getActivity(), movies);
        if(isNetworkAvailable() && movies.size()==0) {
            FetchMovieTask task = new FetchMovieTask(movieAdapter);
            task.execute(Integer.toString(mPage));
        }
        else {
            if(!isNetworkAvailable()){
                Snackbar snackbar = Snackbar
                        .make(MainActivity.coordinatorLayout, "Network Unavailable!" + "\n" + "Please Connect to Internet.", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateList();
                    }
                });
                snackbar.show();
            }
        }
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);
                Intent i = new Intent(getActivity().getApplicationContext(), Details.class);
                i.putExtra("movie", movie);
                startActivity(i);
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
