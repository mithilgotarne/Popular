package com.mithil.popular;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mithil on 11/11/2015.
 */
public class Movie implements Parcelable{
    private SharedPreferences sharedPreferences;
    String id,name, image, image2, overview, rating, date ;
    int isFav;
    public Movie(){
        isFav = 0;
    }

    private Movie(Parcel in){
        isFav = in.readInt();
        id=in.readString();
        name = in.readString();
        image = in.readString();
        image2 = in.readString();
        overview = in.readString();
        rating = in.readString();
        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(isFav);
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(image2);
        parcel.writeString(overview);
        parcel.writeString(rating);
        parcel.writeString(date);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };

    public void save(SharedPreferences s){
        sharedPreferences = s;
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(id,id);
        e.putString("name"+id,name);
        e.putString("image"+id,image);
        e.putString("image2"+id,image2);
        e.putString("overview"+id,overview);
        e.putString("rating"+id,rating);
        e.putString("date"+id,date);
        e.apply();
    }

    public void getSaved(SharedPreferences s, String id){
        sharedPreferences = s;
        this.id = sharedPreferences.getString(id,null);
        name = sharedPreferences.getString("name"+id,null);
        image = sharedPreferences.getString("image"+id,null);
        image2 = sharedPreferences.getString("image2"+id,null);
        overview = sharedPreferences.getString("overview"+id,null);
        rating = sharedPreferences.getString("rating"+id,null);
        date = sharedPreferences.getString("date"+id,null);
    }

}
