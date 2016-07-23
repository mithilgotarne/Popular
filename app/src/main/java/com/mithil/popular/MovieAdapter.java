package com.mithil.popular;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mithil on 11/11/2015.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {
    Context context;
    public MovieAdapter(Activity context, List<Movie> movieList){
        super(context,0,movieList);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if(convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_item_movie,parent,false);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.gridView_item_movie_image);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185" + movie.image)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.no_internet)
                .into(imageView);
        return convertView;

    }
}

