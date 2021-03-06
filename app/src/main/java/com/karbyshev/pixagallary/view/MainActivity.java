package com.karbyshev.pixagallary.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.karbyshev.pixagallary.R;
import com.karbyshev.pixagallary.adapter.MyAdapter;
import com.karbyshev.pixagallary.model.Hit;
import com.karbyshev.pixagallary.model.PostModel;
import com.karbyshev.pixagallary.util.AppController;
import com.karbyshev.pixagallary.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AAH_FabulousFragment.Callbacks{

    private static final String KEY_SELECTED_ITEM = "MainActivity.KEY_SELECTED_ITEM";

    public static int SPAN_COUNT = 1;
    public static String CATEGORY = "all";
    public static String ORIENTATION = "all";

    private RecyclerView mRecyclerView;
    private HashMap<String,String> map;
    private GridLayoutManager mGridLayoutManager;
    private MyAdapter myAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySampleFabFragment dialogFrag = MySampleFabFragment.newInstance(CATEGORY);
                dialogFrag.setParentFab(fab);
                dialogFrag.show(getSupportFragmentManager(), dialogFrag.getTag());
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.my_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(this, SPAN_COUNT);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        myAdapter = new MyAdapter(MainActivity.this);
        mRecyclerView.setAdapter(myAdapter);

        CATEGORY = getSharedPreferences().getString(KEY_SELECTED_ITEM, "all");

        map = new HashMap<>();
        map.put("key", "8334968-4779a336d920b0785293ef347");
        map.put("image_type", "photo");
        map.put("q", "all");
        map.put("orientation", ORIENTATION);
        map.put("category", CATEGORY);
        map.put("per_page", "100");
        map.put("page", "1");
        showData();
    }

    @Override
    public void onResult(Object result) {
        map.put("category", CATEGORY);
        getSharedPreferences().edit().putString(KEY_SELECTED_ITEM, CATEGORY).commit();
        showData();
    }

    private void showData(){
        AppController.getApi().data(map).enqueue(new Callback<PostModel>() {
            private List<Hit> postsList = new ArrayList<>();
            @Override
            public void onResponse(Call<PostModel> call, Response<PostModel> response) {
                PostModel postModel = response.body();
                postsList =postModel.getHits();
                myAdapter.addAll(postsList);
            }

            @Override
            public void onFailure(Call<PostModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred during networking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private SharedPreferences getSharedPreferences() {
        return getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }
}
