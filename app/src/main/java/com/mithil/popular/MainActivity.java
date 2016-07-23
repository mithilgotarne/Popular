package com.mithil.popular;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {
    public static String POSITION = "POSITION";
    public static final String PREFS = "myPrefs";
    public static final String TAB = "TABNO";
    public static ArrayList<String> favMovies;
    int tabPosition;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ViewPager viewPager;
    TabLayout tabLayout;
    static CoordinatorLayout coordinatorLayout;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    @Override
    protected void onPause() {
        editor = sharedPreferences.edit();
        editor.putInt(TAB, tabLayout.getSelectedTabPosition());
        editor.putStringSet("favMovies", new HashSet<String>(favMovies));
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        tabPosition = sharedPreferences.getInt(TAB, 0);
        favMovies =new ArrayList<>(sharedPreferences.getStringSet("favMovies",new HashSet<String>()));
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MoviePagerFragmentAdapter(getSupportFragmentManager(),
                MainActivity.this));

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(tabPosition);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

}
